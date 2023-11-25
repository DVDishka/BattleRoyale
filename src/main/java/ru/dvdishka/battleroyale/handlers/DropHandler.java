package ru.dvdishka.battleroyale.handlers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.ConfigVariables;
import ru.dvdishka.battleroyale.logic.Logger;
import ru.dvdishka.battleroyale.logic.Zone;
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainer;
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainerStage;
import ru.dvdishka.battleroyale.logic.classes.drop.DropType;
import ru.dvdishka.battleroyale.logic.event.DropClickEvent;
import ru.dvdishka.battleroyale.logic.event.DropCreateEvent;

import java.util.Random;

public class DropHandler implements Listener {

    @EventHandler
    public void onDropCreateEvent(DropCreateEvent event) {

        if (!Common.isGameStarted) {
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

            String worldName = dropContainer.getLocation().getWorld().getName();
            TextColor worldNameColor = NamedTextColor.WHITE;

            if (worldName.equals("world")) {
                worldName = "overworld";
                worldNameColor = NamedTextColor.DARK_GREEN;
            }

            if (worldName.equals("the_nether")) {
                worldName = "nether";
                worldNameColor = NamedTextColor.DARK_RED;
            }

            if (worldName.equals("the_end")) {
                worldName = "end";
                worldNameColor = NamedTextColor.DARK_PURPLE;
            }

            worldName = worldName.toUpperCase();

            Component message = Component.empty();
            Component followButton = Component.empty();

            followButton = followButton
                    .append(Component.text("[FOLLOW]")
                            .color(NamedTextColor.GREEN)
                            .decorate(TextDecoration.BOLD)
                            .clickEvent(ClickEvent.runCommand("/battleroyale drop follow " + "\"" + dropContainer.getName() + "\"")));

            message = message
                    .append(Component.newline())
                    .append(Component.text("-".repeat(26))
                            .color(NamedTextColor.RED)
                            .decorate(TextDecoration.BOLD))
                    .append(Component.newline());

            message = message
                    .append(Component.text("New drop container!")
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD))
                    .append(Component.newline())
                    .append(Component.text("-".repeat(27))
                            .color(NamedTextColor.YELLOW))
                    .append(Component.newline())
                    .append(Component.text(worldName)
                            .color(worldNameColor))
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
                    .append(followButton)
                    .append(Component.newline());

            message = message
                    .append(Component.text("-".repeat(26))
                            .color(NamedTextColor.RED)
                            .decorate(TextDecoration.BOLD))
                    .append(Component.newline());

            player.sendMessage(message);

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 75, 100);
        }

        Logger.getLogger().warn(String.valueOf(dropContainer.getLocation().getBlockX()));
        Logger.getLogger().warn(String.valueOf(dropContainer.getLocation().getBlockY()));
        Logger.getLogger().warn(String.valueOf(dropContainer.getLocation().getBlockZ()));
    }

    @EventHandler
    public void onDropClickEvent(DropClickEvent event) {

        if (event.getDropContainer().getStage().equals(DropContainerStage.PRE_CLICK_STAGE)) {

            event.getPlayer().playSound(event.getDropContainer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1);
            event.getDropContainer().startOpenCountdown();

        } else if (event.getDropContainer().getStage().equals(DropContainerStage.OPEN_STAGE)) {

            event.getPlayer().playSound(event.getDropContainer().getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 100, 1);
            event.getPlayer().openInventory(event.getDropContainer().getInventory().getInventory());

        } else if (event.getDropContainer().getStage().equals(DropContainerStage.OPENING_STAGE)) {

            event.getPlayer().playSound(event.getDropContainer().getLocation(), org.bukkit.Sound.BLOCK_ANVIL_PLACE, 100, 1);
        }
    }

    private DropContainer generateDropContainer(DropType dropType, World world) {
        
        int x = new Random().nextInt(Zone.getInstance().getNewLeftBorder(), Zone.getInstance().getNewRightBorder() + 1);
        int z = new Random().nextInt(Zone.getInstance().getNewLowerBorder(), Zone.getInstance().getNewUpperBorder() + 1);
        int y = new Random().nextInt(ConfigVariables.minDropSpawnY, ConfigVariables.maxDropSpawnY + 1);

        return new DropContainer(dropType, new Location(world, x, y, z), ConfigVariables.dropOpenTime);
    }
}
