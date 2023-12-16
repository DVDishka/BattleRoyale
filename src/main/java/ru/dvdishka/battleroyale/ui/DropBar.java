package ru.dvdishka.battleroyale.ui;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.Scheduler;
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainer;
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainerStage;

import java.util.Collection;
import java.util.HashMap;

public class DropBar {

    private String worldName;
    private DropContainer dropContainer;
    private Location coordinates;
    private final TextColor color = NamedTextColor.DARK_AQUA;
    private boolean isActive = false;

    private ScheduledTask updateTask;

    private final Player owner;

    private static final HashMap<String, DropBar> instances = new HashMap<>();

    private DropBar(Player owner) {
        this.owner = owner;
    }

    public static DropBar getInstance(Player player) {

        if (instances.containsKey(player.getName())) {
            return instances.get(player.getName());
        }
        instances.put(player.getName(), new DropBar(player));

        final DropBar instance = instances.get(player.getName());

        Scheduler.getScheduler().runSyncRepeatingTask(Common.plugin, (scheduledTask) -> {
            instance.update();
            if (!instance.isActive) {
                scheduledTask.cancel();
            }
        }, 10, 10);

        return instance;
    }

    public static Collection<DropBar> getInstances() {
        return instances.values();
    }

    public DropContainer getInformation() {
        return dropContainer;
    }

    @SuppressWarnings("unused")
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {

        if (!isActive && this.isActive) {
            this.updateTask.cancel();
        }

        if (isActive && !this.isActive) {

            this.updateTask = Scheduler.getScheduler().runSyncRepeatingTask(Common.plugin, (scheduledTask) -> {

                this.update();
            }, 10, 10);
        }
        this.isActive = isActive;
    }

    public void setInformation(DropContainer dropContainer) {

        this.setActive(false);

        this.dropContainer = dropContainer;

        this.coordinates = dropContainer.getLocation();

        if (dropContainer.getLocation().getWorld().getName().equals("world")) {
            this.worldName = "overworld";
        } else {
            this.worldName = dropContainer.getLocation().getWorld().getName();
        }

        if (dropContainer.getLocation().getWorld().getName().equals("the_nether")) {
            this.worldName = "nether";
        } else {
            this.worldName = dropContainer.getLocation().getWorld().getName();
        }

        if (dropContainer.getLocation().getWorld().getName().equals("the_end")) {
            this.worldName = "end";
        } else {
            this.worldName = dropContainer.getLocation().getWorld().getName();
        }

        this.worldName = this.worldName.toUpperCase();

        this.setActive(true);
    }

    public void update() {

        if (dropContainer == null) {
            return;
        }

        Component actionBar = Component.text(worldName)
                .decorate(TextDecoration.BOLD)
                .append(Component.space())
                .append(Component.text("X:"))
                .append(Component.text(coordinates.getBlockX()))
                .append(Component.space())
                .append(Component.text("Y:"))
                .append(Component.text(coordinates.getBlockY()))
                .append(Component.space())
                .append(Component.text("Z:"))
                .append(Component.text(coordinates.getBlockZ()))
                .color(color)
                .append(Component.space())
                .append(this.dropContainer.getStage().getForPercentStageComponent());

        if (this.dropContainer.getStage().equals(DropContainerStage.OPENING_STAGE)) {

            actionBar = actionBar
                    .append(Component.text((int) (((float) (this.dropContainer.getMaxTimeToOpen() - this.dropContainer.getTimeToOpen())) / (((float) dropContainer.getMaxTimeToOpen()) / 100.0)))
                            .decorate(TextDecoration.BOLD)
                            .color(NamedTextColor.RED))
                    .append(Component.text("%")
                            .decorate(TextDecoration.BOLD)
                            .color(NamedTextColor.RED))
                    .append(Component.text("]")
                            .decorate(TextDecoration.BOLD)
                            .color(NamedTextColor.RED));
        }

        owner.sendActionBar(actionBar);
    }

    public void unregister() {

        this.setActive(false);
        instances.remove(this.owner.getName());
    }
}
