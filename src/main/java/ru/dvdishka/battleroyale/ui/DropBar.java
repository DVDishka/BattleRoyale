package ru.dvdishka.battleroyale.ui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.Scheduler;
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainer;
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainerStage;

import java.util.ArrayList;
import java.util.HashMap;

public class DropBar {

    private String worldName;
    private DropContainer dropContainer;
    private Location coordinates;
    private final TextColor color = NamedTextColor.DARK_AQUA;
    private boolean isActive = false;

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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        if (isActive && !this.isActive) {
            Scheduler.getScheduler().runSyncRepeatingTask(Common.plugin, (scheduledTask) -> {
                this.update();
                if (!this.isActive) {
                    scheduledTask.cancel();
                }
            }, 10, 10);
        }
        this.isActive = isActive;
    }

    public void setInformation(DropContainer dropContainer) {

        this.dropContainer = dropContainer;
        this.isActive = true;

        this.coordinates = dropContainer.getLocation();

        if (dropContainer.getLocation().getWorld().getName().equals("world")) {
            this.worldName = "overworld";
        } else {
            this.worldName = dropContainer.getLocation().getWorld().getName();
        }
    }

    public void update() {

        owner.sendActionBar(
                Component.text(worldName)
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
                        .append(this.dropContainer.getStage().getStageComponent()));
    }
}
