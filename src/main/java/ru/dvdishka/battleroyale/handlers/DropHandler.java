package ru.dvdishka.battleroyale.handlers;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import ru.dvdishka.battleroyale.handlers.commands.common.Permission;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.ConfigVariables;
import ru.dvdishka.battleroyale.logic.Logger;
import ru.dvdishka.battleroyale.logic.Zone;
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainer;
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainerStage;
import ru.dvdishka.battleroyale.logic.classes.drop.DropType;
import ru.dvdishka.battleroyale.logic.event.drop.DropClickEvent;
import ru.dvdishka.battleroyale.logic.event.drop.DropCreateEvent;

import java.util.Random;

public class DropHandler implements Listener {

    @EventHandler
    public void onDropCreateEvent(DropCreateEvent event) {

        if (!Common.isGameStarted || DropType.getDropTypes().isEmpty()) {
            return;
        }

        DropContainer dropContainer = generateDropContainer(event.getDropType(), event.getWorld());

        dropContainer.getLocation().getBlock().setType(Material.PLAYER_HEAD);

        Skull dropContainerBlock = (Skull) dropContainer.getLocation().getBlock().getState();
        dropContainerBlock.setPlayerProfile(Bukkit.createProfile("FlyntCoal"));

        MetadataValue dropContainerMetadataValue = new FixedMetadataValue(Common.plugin, dropContainer);
        dropContainerBlock.setMetadata("dropContainer", dropContainerMetadataValue);

        dropContainerBlock.update();

        for (Player player : Bukkit.getOnlinePlayers()) {

            Component header = Component.empty();
            Component text = Component.empty();

            Component followButton = Component.empty();

            followButton = followButton
                    .append(Component.text("[FOLLOW]")
                            .color(NamedTextColor.GREEN)
                            .decorate(TextDecoration.BOLD)
                            .clickEvent(ClickEvent.runCommand("/battleroyale drop follow " + "\"" + dropContainer.getName() + "\"")));

            Component deleteButton = Component.empty();

            deleteButton = deleteButton
                    .append(Component.text("[DELETE]")
                            .color(NamedTextColor.RED)
                            .decorate(TextDecoration.BOLD)
                            .clickEvent(ClickEvent.runCommand("/battleroyale drop delete " + "\"" + dropContainer.getName() + "\"")));

            header = header
                    .append(Component.text("New drop container!")
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD));
            text = text
                    .append(dropContainer.getWorldComponent())
                    .append(Component.space())
                    .append(Component.text("X:"))
                    .append(Component.space())
                    .append(Component.text(dropContainer.getLocation().getBlockX()))
                    .append(Component.space())
                    .append(Component.text("Y:"))
                    .append(Component.space())
                    .append(Component.text(dropContainer.getLocation().getBlockY()))
                    .append(Component.space())
                    .append(Component.text("Z:"))
                    .append(Component.space())
                    .append(Component.text(dropContainer.getLocation().getBlockZ()))
                    .append(Component.newline())
                    .append(Component.text("-".repeat(27))
                            .color(NamedTextColor.YELLOW))
                    .append(Component.newline())
                    .append(followButton);

            if (player.hasPermission(Permission.DROP.getStringPermission())) {
                text = text
                        .append(Component.space())
                        .append(deleteButton);
            }

            Common.sendNotification(header, text, player);
        }
    }

    @EventHandler
    public void onDropClickEvent(DropClickEvent event) {

        if (event.getDropContainer().getStage().equals(DropContainerStage.PRE_CLICK_STAGE)) {

            event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1);
            event.getDropContainer().startOpenCountdown();

        } else if (event.getDropContainer().getStage().equals(DropContainerStage.OPEN_STAGE)) {

            event.getPlayer().playSound(event.getPlayer(), org.bukkit.Sound.UI_BUTTON_CLICK, 100, 1);
            event.getPlayer().openInventory(event.getDropContainer().getInventory().getInventory());

        } else if (event.getDropContainer().getStage().equals(DropContainerStage.OPENING_STAGE)) {

            event.getPlayer().playSound(event.getPlayer(), org.bukkit.Sound.BLOCK_ANVIL_PLACE, 100, 1);
        }
    }

    private DropContainer generateDropContainer(DropType dropType, World world) {
        
        int x = new Random().nextInt(Zone.getInstance().getNewLeftBorder(), Zone.getInstance().getNewRightBorder() + 1);
        int z = new Random().nextInt(Zone.getInstance().getNewLowerBorder(), Zone.getInstance().getNewUpperBorder() + 1);
        int y = new Random().nextInt(ConfigVariables.minDropSpawnY, ConfigVariables.maxDropSpawnY + 1);

        return new DropContainer(dropType, new Location(world, x, y, z), ConfigVariables.dropOpenTime);
    }

    @org.bukkit.event.EventHandler
    public void onDropContainerExplode(EntityExplodeEvent event) {

        for (Block block : event.blockList()) {

            if (block.hasMetadata("dropContainer")) {

                event.setCancelled(true);
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onDropContainerExplode(BlockExplodeEvent event) {

        for (Block block : event.blockList()) {

            if (block.hasMetadata("dropContainer")) {

                event.setCancelled(true);
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onDropContainerLiquidBreak(BlockFromToEvent event) {

        if (event.getToBlock().hasMetadata("dropContainer")) {
            event.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onDropContainerBreak(BlockBreakEvent event) {

        if (event.getBlock().hasMetadata("dropContainer")) {

            event.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onDropContainerDestroy(BlockDestroyEvent event) {

        if (event.getBlock().hasMetadata("dropContainer")) {

            event.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onDropContainerBurn(BlockBurnEvent event) {

        if (event.getBlock().hasMetadata("dropContainer")) {

            event.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onDropContainerPistonExtend(BlockPistonExtendEvent event) {

        for (Block block : event.getBlocks()) {

            if (block.hasMetadata("dropContainer")) {

                event.setCancelled(true);
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onDropContainerPistonRetract(BlockPistonRetractEvent event) {

        for (Block block : event.getBlocks()) {

            if (block.hasMetadata("dropContainer")) {

                event.setCancelled(true);
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onBlockClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().hasMetadata("dropContainer")) {
            if (event.getAction().isRightClick()) {
                Bukkit.getPluginManager().callEvent(new DropClickEvent(DropContainer.getContainerByLocation(event.getClickedBlock().getLocation()), event.getPlayer()));
            }
        }
    }
}
