package ru.dvdishka.battleroyale.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import ru.dvdishka.battleroyale.common.Common;
import ru.dvdishka.battleroyale.common.Logger;
import ru.dvdishka.battleroyale.common.Scheduler;

public class ZoneBordersChangeTask implements Runnable {

    private final int oldZoneDiameter;
    private final int newZoneDiameter;
    private final long timeSeconds;
    private final double oldZoneCenterX;
    private final double oldZoneCenterZ;
    private final double newZoneCenterX;
    private final double newZoneCenterZ;

    public ZoneBordersChangeTask(int oldZoneDiameter, int newZoneDiameter, long timeSeconds, double oldZoneCenterX, double oldZoneCenterZ, double newZoneCenterX, double newZoneCenterZ) {
        this.oldZoneDiameter = oldZoneDiameter;
        this.newZoneDiameter = newZoneDiameter;
        this.timeSeconds = timeSeconds;
        this.oldZoneCenterX = oldZoneCenterX;
        this.oldZoneCenterZ = oldZoneCenterZ;
        this.newZoneCenterX = newZoneCenterX;
        this.newZoneCenterZ = newZoneCenterZ;
    }

    @Override
    public void run() {

        double borderSizeStep = ((double) (newZoneDiameter - oldZoneDiameter)) / (timeSeconds * 2);
        double borderCenterStepX = (newZoneCenterX - oldZoneCenterX) / (timeSeconds * 2);
        double borderCenterStepZ = (newZoneCenterZ - oldZoneCenterZ) / (timeSeconds * 2);

        double currentBorderSize = oldZoneDiameter;
        double currentZoneCenterX = oldZoneCenterX;
        double currentZoneCenterZ = oldZoneCenterZ;

        for (long i = 0; i < timeSeconds * 2; i++) {

            currentBorderSize += borderSizeStep;
            currentZoneCenterX += borderCenterStepX;
            currentZoneCenterZ += borderCenterStepZ;

            final double borderSize = currentBorderSize;
            final double zoneCenterX = currentZoneCenterX;
            final double zoneCenterZ = currentZoneCenterZ;
            final long delay;
            if (i > 0) {
                delay = i * 10;
            } else {
                delay = 1;
            }

            Scheduler.getScheduler().runSyncDelayed(Common.plugin, () -> {
                if (delay / 10 != timeSeconds * 2 - 1) {
                    for (World world : Bukkit.getWorlds()) {
                        world.getWorldBorder().setSize(borderSize);
                        world.getWorldBorder().setCenter(zoneCenterX, zoneCenterZ);
                    }
                } else {
                    for (World world : Bukkit.getWorlds()) {
                        world.getWorldBorder().setSize(newZoneDiameter);
                        world.getWorldBorder().setCenter(newZoneCenterX, newZoneCenterZ);
                    }
                }
            }, delay);
        }
    }
}
