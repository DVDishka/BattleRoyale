package ru.dvdishka.battleroyale.logic.classes.drop;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.Scheduler;
import ru.dvdishka.battleroyale.ui.DropBar;

import java.util.Collection;
import java.util.HashMap;

public class DropContainer {

    private final DropType dropType;
    private final Location location;
    private final DropContainerInventory inventory;

    private DropContainerStage stage = DropContainerStage.PRE_CLICK_STAGE;
    private int timeToOpen;
    private final int maxTimeToOpen;

    private static final HashMap<Location, DropContainer> dropContainers = new HashMap<>();

    public DropContainer(DropType dropType, Location location, int timeToOpen) {

        this.dropType = dropType;
        this.location = location;
        this.timeToOpen = timeToOpen;
        this.maxTimeToOpen = timeToOpen;
        this.inventory = new DropContainerInventory();

        for (ItemStack item : dropType.getItems()) {
            this.inventory.getInventory().addItem(item);
        }

        dropContainers.put(this.location, this);
    }

    public String getName() {

        return location.getWorld().getName() + " " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
    }

    @SuppressWarnings("unused")
    public DropType getDropType() {
        return dropType;
    }

    public Location getLocation() {
        return location;
    }

    @SuppressWarnings("unused")
    public void nextStage() {
        if (this.stage.equals(DropContainerStage.PRE_CLICK_STAGE)) {
            this.stage = DropContainerStage.OPENING_STAGE;
        }
        if (this.stage.equals(DropContainerStage.OPENING_STAGE)) {
            this.stage = DropContainerStage.OPEN_STAGE;
        }
    }

    @SuppressWarnings("unused")
    public void setStage(DropContainerStage stage) {
        this.stage = stage;
    }

    public DropContainerStage getStage() {
        return this.stage;
    }

    public int getTimeToOpen() {
        return this.timeToOpen;
    }

    public int getMaxTimeToOpen() {
        return this.maxTimeToOpen;
    }

    public void delete() {

        this.getLocation().getBlock().setType(Material.AIR);
        this.getLocation().getBlock().removeMetadata("dropContainer", Common.plugin);

        for (DropBar dropBar : DropBar.getInstances()) {
            if (dropBar.getInformation() == this) {
                dropBar.setActive(false);
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {

            if (Common.players.contains(player.getName())) {

                String worldName = this.getLocation().getWorld().getName();
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

                Component header = Component.empty();
                Component text = Component.empty();

                header = header
                        .append(Component.text("Drop container has been deleted!")
                                .color(NamedTextColor.GOLD)
                                .decorate(TextDecoration.BOLD));

                text = text
                        .append(Component.text(worldName)
                                .color(worldNameColor))
                        .append(Component.space())
                        .append(Component.text("X:"))
                        .append(Component.space())
                        .append(Component.text(this.getLocation().getBlockX()))
                        .append(Component.space())
                        .append(Component.text("Y:"))
                        .append(Component.space())
                        .append(Component.text(this.getLocation().getBlockY()))
                        .append(Component.space())
                        .append(Component.text("Z:"))
                        .append(Component.space())
                        .append(Component.text(this.getLocation().getBlockZ()));


                Common.sendNotification(header, text, player);
            }
        }

        dropContainers.remove(this.location);
    }

    public static DropContainer getContainerByLocation(Location location) {
        return dropContainers.get(location);
    }

    public static Collection<DropContainer> getContainerList() {
        return dropContainers.values();
    }

    @SuppressWarnings("UnusedAssignment")
    public static DropContainer parseFromString(String dropContainerString) {

        if (!Common.isGameStarted) {
            return null;
        }

        String[] dropName = dropContainerString.split(" ");

        World world;

        try {
            world = Bukkit.getWorld(dropName[0]);
            if (world == null) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        int x = 0;
        try {
            x = Integer.parseInt(dropName[1]);
        } catch (Exception e) {
            return null;
        }

        int y = 0;
        try {
            y = Integer.parseInt(dropName[2]);
        } catch (Exception e) {
            return null;
        }

        int z = 0;
        try {
            z = Integer.parseInt(dropName[3]);
        } catch (Exception e) {
            return null;
        }

        return DropContainer.getContainerByLocation(new Location(world, x, y, z));
    }

    public void startOpenCountdown() {

        this.stage = DropContainerStage.OPENING_STAGE;

        Scheduler.getScheduler().runSyncRepeatingTask(Common.plugin, (scheduledTask) -> {
            if (timeToOpen > 0) {
                for (Player player : this.location.getNearbyPlayers(25)) {
                    player.playSound(player, Sound.UI_BUTTON_CLICK, 100, 1);
                }
                this.timeToOpen--;
            } else {
                for (Player player : this.location.getNearbyPlayers(25)) {
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1000, 10);
                    player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1000, 1);
                }
                this.stage = DropContainerStage.OPEN_STAGE;
                scheduledTask.cancel();
            }
        }, 20, 20);
    }

    public DropContainerInventory getInventory() {
        return inventory;
    }
}
