package ru.dvdishka.battleroyale.logic;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Random;

public class Zone {

    private int oldZoneDiameter;
    private int newZoneDiameter;
    private int oldZoneCenterX;
    private int newZoneCenterX;
    private int oldZoneCenterZ;
    private int newZoneCenterZ;

    private boolean isActive = true;
    private volatile boolean isZoneMoving = false;
    private HashSet<ScheduledTask> currentMoveTasks = new HashSet<>();

    private static Zone instance = null;

    private Zone(int oldZoneDiameter, int newZoneDiameter, int oldZoneCenterX, int oldZoneCenterZ, int newZoneCenterX, int newZoneCenterZ) {

        this.oldZoneDiameter = oldZoneDiameter;
        this.newZoneDiameter = newZoneDiameter;
        this.oldZoneCenterX = oldZoneCenterX;
        this.oldZoneCenterZ = oldZoneCenterZ;
        this.newZoneCenterX = newZoneCenterX;
        this.newZoneCenterZ = newZoneCenterZ;
    }

    public static Zone getInstance() {

        if (instance == null) {
            instance = new Zone(
                    ConfigVariables.defaultWorldBorderDiameter,
                    ConfigVariables.defaultWorldBorderDiameter,
                    0,
                    0,
                    0,
                    0);
        }
        return instance;
    }

    public static void unregister() {

        try {
            instance.stopMoving();
            instance = null;
        } catch (Exception ignored) {}
    }

    public void setVariables(int oldZoneDiameter, int newZoneDiameter, int oldZoneCenterX, int oldZoneCenterZ, int newZoneCenterX, int newZoneCenterZ) {

        this.oldZoneDiameter = oldZoneDiameter;
        this.newZoneDiameter = newZoneDiameter;
        this.oldZoneCenterX = oldZoneCenterX;
        this.oldZoneCenterZ = oldZoneCenterZ;
        this.newZoneCenterX = newZoneCenterX;
        this.newZoneCenterZ = newZoneCenterZ;
    }

    public void changeBorders(int oldZoneDiameter, int newZoneDiameter, long timeSeconds, int oldZoneCenterX, int oldZoneCenterZ, int newZoneCenterX, int newZoneCenterZ) {

        if (!isActive) {
            return;
        }

        double borderSizeStep = ((double) (newZoneDiameter - oldZoneDiameter)) / (timeSeconds * 2);
        double borderCenterStepX = ((double) (newZoneCenterX - oldZoneCenterX)) / (timeSeconds * 2);
        double borderCenterStepZ = ((double) (newZoneCenterZ - oldZoneCenterZ)) / (timeSeconds * 2);

        double currentBorderSize = oldZoneDiameter;
        double currentZoneCenterX = oldZoneCenterX;
        double currentZoneCenterZ = oldZoneCenterZ;

        this.oldZoneDiameter = oldZoneDiameter;
        this.newZoneDiameter = newZoneDiameter;
        this.oldZoneCenterX = oldZoneCenterX;
        this.oldZoneCenterZ = oldZoneCenterZ;
        this.newZoneCenterX = newZoneCenterX;
        this.newZoneCenterZ = newZoneCenterZ;

        this.isZoneMoving = true;
        this.currentMoveTasks.clear();

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

            ScheduledTask moveTask = Scheduler.getScheduler().runSyncDelayed(Common.plugin, (scheduledTask) -> {
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
                    this.isZoneMoving = false;
                }
            }, delay);

            this.currentMoveTasks.add(moveTask);
        }
    }

    public int generateRandomZoneCenterX(int previousZoneRadius, int nextZoneRadius, int currentZoneCenterX) {

        int nextZoneCenterX = new Random().nextInt(currentZoneCenterX - previousZoneRadius + nextZoneRadius,
                currentZoneCenterX + previousZoneRadius - nextZoneRadius + 1);

        return nextZoneCenterX;
    }

    public int generateRandomZoneCenterZ(int previousZoneRadius, int nextZoneRadius, int currentZoneCenterZ) {

        int nextZoneCenterZ = new Random().nextInt(currentZoneCenterZ - previousZoneRadius + nextZoneRadius,
                currentZoneCenterZ + previousZoneRadius - nextZoneRadius + 1);

        return nextZoneCenterZ;
    }

    public void moveZone(int xMove, int zMove, int duration, int steps) {

        if (!isActive) {
            return;
        }

        int step = duration * 20 / steps;

        isZoneMoving = true;

        for (int i = step; i <= duration * 20; i += step) {

            final int delay = i;

            ScheduledTask moveTask = Scheduler.getScheduler().runSyncDelayed(Common.plugin, (scheduledTask) -> {

                if (!Common.isGameStarted) {
                    return;
                }

                for (World world : Bukkit.getWorlds()) {

                    int x = 0, z = 0;
                    if (xMove != 0) {
                        x = xMove;
                    }
                    if (zMove != 0) {
                        z = zMove;
                    }

                    world.getWorldBorder().setCenter(world.getWorldBorder().getCenter().x() + x,
                            world.getWorldBorder().getCenter().z() + z);
                }

                if (delay + step >= duration * 20) {
                    isZoneMoving = false;
                }
            }, delay);

            this.currentMoveTasks.add(moveTask);
        }
    }

    public void stopMoving() {
        this.isActive = false;
        this.isZoneMoving = false;
        for (ScheduledTask moveTask : this.currentMoveTasks) {
            moveTask.cancel();
        }
        this.currentMoveTasks.clear();
    }

    public boolean isZoneMoving() {
        return isZoneMoving;
    }

    public void setZoneMoving(boolean zoneMoving) {
        this.isZoneMoving = zoneMoving;
    }

    public int getNewZoneDiameter() {
        return newZoneDiameter;
    }

    public int getNewZoneCenterX() {
        return newZoneCenterX;
    }

    public int getNewZoneCenterZ() {
        return newZoneCenterZ;
    }

    public int getNewLeftBorder() {
        return ((int) newZoneCenterX) - (newZoneDiameter / 2);
    }

    public int getNewRightBorder() {
        return ((int) newZoneCenterX) + (newZoneDiameter / 2);
    }

    public int getNewLowerBorder() {
        return ((int) newZoneCenterZ) - (newZoneDiameter / 2);
    }

    public int getNewUpperBorder() {
        return ((int) newZoneCenterZ) + (newZoneDiameter / 2);
    }

    public int getCurrentZoneDiameter() {
        return (int) Common.overWorld.getWorldBorder().getSize();
    }

    public int getCurrentZoneCenterX() {
        return Common.overWorld.getWorldBorder().getCenter().getBlockX();
    }

    public int getCurrentZoneCenterZ() {
        return Common.overWorld.getWorldBorder().getCenter().getBlockZ();
    }

    public int getCurrentLeftBorder() {
        return Common.overWorld.getWorldBorder().getCenter().getBlockX() - (((int) Common.overWorld.getWorldBorder().getSize()) / 2);
    }

    public int getCurrentRightBorder() {
        return Common.overWorld.getWorldBorder().getCenter().getBlockX() + (((int) Common.overWorld.getWorldBorder().getSize()) / 2);
    }

    public int getCurrentLowerBorder() {
        return Common.overWorld.getWorldBorder().getCenter().getBlockZ() - (((int) Common.overWorld.getWorldBorder().getSize()) / 2);
    }

    public int getCurrentUpperBorder() {
        return Common.overWorld.getWorldBorder().getCenter().getBlockZ() + (((int) Common.overWorld.getWorldBorder().getSize()) / 2);
    }

    public double getCurrentZoneFloatDiameter() {
        return Common.overWorld.getWorldBorder().getSize();
    }

    public double getCurrentZoneFloatCenterX() {
        return Common.overWorld.getWorldBorder().getCenter().getX();
    }

    public double getCurrentZoneFloatCenterZ() {
        return Common.overWorld.getWorldBorder().getCenter().getZ();
    }

    public double getCurrentLeftFloatBorder() {
        return Common.overWorld.getWorldBorder().getCenter().getX() - ((Common.overWorld.getWorldBorder().getSize()) / 2);
    }

    public double getCurrentRightFloatBorder() {
        return Common.overWorld.getWorldBorder().getCenter().getX() + ((Common.overWorld.getWorldBorder().getSize()) / 2);
    }

    public double getCurrentLowerFloatBorder() {
        return Common.overWorld.getWorldBorder().getCenter().getZ() - ((Common.overWorld.getWorldBorder().getSize()) / 2);
    }

    public double getCurrentUpperFloatBorder() {
        return Common.overWorld.getWorldBorder().getCenter().getBlockZ() + ((Common.overWorld.getWorldBorder().getSize()) / 2);
    }

    public int getOldZoneDiameter() {
        return oldZoneDiameter;
    }

    public int getOldZoneCenterX() {
        return oldZoneCenterX;
    }

    public int getOldZoneCenterZ() {
        return oldZoneCenterZ;
    }

    public int getOldLeftBorder() {
        return oldZoneCenterX - (oldZoneDiameter / 2);
    }

    public int getOldRightBorder() {
        return oldZoneCenterX + (oldZoneDiameter / 2);
    }

    public int getOldLowerBorder() {
        return oldZoneCenterZ - (oldZoneDiameter / 2);
    }

    public int getOldUpperBorder() {
        return oldZoneCenterZ + (oldZoneDiameter / 2);
    }
}
