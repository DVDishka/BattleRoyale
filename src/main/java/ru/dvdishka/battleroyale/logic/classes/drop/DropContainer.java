package ru.dvdishka.battleroyale.logic.classes.drop;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.Scheduler;

import java.util.Collection;
import java.util.HashMap;

public class DropContainer {

    private final DropType dropType;
    private final Location location;
    private final DropContainerInventory inventory;

    private DropContainerStage stage = DropContainerStage.PRE_CLICK_STAGE;
    private int timeToOpen;

    private static final HashMap<Location, DropContainer> dropContainers = new HashMap<>();

    public DropContainer(DropType dropType, Location location, int timeToOpen) {

        this.dropType = dropType;
        this.location = location;
        this.timeToOpen = timeToOpen;
        this.inventory = new DropContainerInventory();

        for (ItemStack item : dropType.getItems()) {
            this.inventory.getInventory().addItem(item);
        }

        dropContainers.put(this.location, this);
    }

    public String getName() {

        return location.getWorld().getName() + " " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
    }

    public DropType getDropType() {
        return dropType;
    }

    public Location getLocation() {
        return location;
    }

    public void nextStage() {
        if (this.stage.equals(DropContainerStage.PRE_CLICK_STAGE)) {
            this.stage = DropContainerStage.OPENING_STAGE;
        }
        if (this.stage.equals(DropContainerStage.OPENING_STAGE)) {
            this.stage = DropContainerStage.OPEN_STAGE;
        }
    }

    public void setStage(DropContainerStage stage) {
        this.stage = stage;
    }

    public DropContainerStage getStage() {
        return this.stage;
    }

    public int getTimeToOpen() {
        return this.timeToOpen;
    }

    public static DropContainer getContainerByLocation(Location location) {
        return dropContainers.get(location);
    }

    public static Collection<DropContainer> getContainerList() {
        return dropContainers.values();
    }

    public void startOpenCountdown() {

        this.stage = DropContainerStage.OPENING_STAGE;

        Scheduler.getScheduler().runSyncRepeatingTask(Common.plugin, (scheduledTask) -> {
            if (timeToOpen > 0) {
                for (Player player : this.location.getNearbyPlayers(50)) {
                    player.playSound(this.location, Sound.UI_BUTTON_CLICK, 100, 1);
                }
                this.timeToOpen--;
            } else {
                for (Player player : this.location.getNearbyPlayers(50)) {
                    player.playSound(this.location, Sound.ENTITY_PLAYER_LEVELUP, 1000, 10);
                    player.playSound(this.location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1000, 1);
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
