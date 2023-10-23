package ru.dvdishka.battleroyale.classes;

import org.bukkit.Bukkit;
import org.bukkit.World;
import ru.dvdishka.battleroyale.common.Common;
import ru.dvdishka.battleroyale.common.ConfigVariables;
import ru.dvdishka.battleroyale.common.Logger;
import ru.dvdishka.battleroyale.common.Scheduler;

import java.util.Random;

public class Zone {

    private int oldZoneDiameter;
    private int newZoneDiameter;
    private int oldZoneCenterX;
    private int oldZoneCenterZ;
    private int newZoneCenterX;
    private int newZoneCenterZ;

    private volatile boolean isZoneMoving = false;

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

    public void setVariables(int oldZoneDiameter, int newZoneDiameter, int oldZoneCenterX, int oldZoneCenterZ, int newZoneCenterX, int newZoneCenterZ) {

        this.oldZoneDiameter = oldZoneDiameter;
        this.newZoneDiameter = newZoneDiameter;
        this.oldZoneCenterX = oldZoneCenterX;
        this.oldZoneCenterZ = oldZoneCenterZ;
        this.newZoneCenterX = newZoneCenterX;
        this.newZoneCenterZ = newZoneCenterZ;
    }

    public void changeBorders(int oldZoneDiameter, int newZoneDiameter, long timeSeconds, int oldZoneCenterX, int oldZoneCenterZ, int newZoneCenterX, int newZoneCenterZ) {

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
                    this.isZoneMoving = false;
                }
            }, delay);
        }
    }

    public int generateRandomZoneCenterX(int previousZoneRadius, int nextZoneRadius, int currentZoneCenterX) {

        int nextZoneCenterX = new Random().nextInt(currentZoneCenterX - previousZoneRadius + nextZoneRadius,
                currentZoneCenterX + previousZoneRadius - nextZoneRadius + 1);
        Logger.getLogger().devWarn("X: " + String.valueOf((currentZoneCenterX - previousZoneRadius + nextZoneRadius)) + " - " +
                String.valueOf(currentZoneCenterX + previousZoneRadius - nextZoneRadius + 1));
        Logger.getLogger().devWarn(String.valueOf(nextZoneCenterX));

        return nextZoneCenterX;
    }

    public int generateRandomZoneCenterZ(int previousZoneRadius, int nextZoneRadius, int currentZoneCenterZ) {

        int nextZoneCenterZ = new Random().nextInt(currentZoneCenterZ - previousZoneRadius + nextZoneRadius,
                currentZoneCenterZ + previousZoneRadius - nextZoneRadius + 1);
        Logger.getLogger().devWarn("Z: " + String.valueOf((currentZoneCenterZ - previousZoneRadius + nextZoneRadius)) + " - " +
                String.valueOf(currentZoneCenterZ + previousZoneRadius - nextZoneRadius + 1));
        Logger.getLogger().devWarn(String.valueOf(nextZoneCenterZ));

        return nextZoneCenterZ;
    }

    public boolean isZoneMoving() {
        return isZoneMoving;
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
        return (int) Bukkit.getWorld("world").getWorldBorder().getSize();
    }

    public int getCurrentZoneCenterX() {
        return Bukkit.getWorld("world").getWorldBorder().getCenter().getBlockX();
    }

    public int getCurrentZoneCenterZ() {
        return Bukkit.getWorld("world").getWorldBorder().getCenter().getBlockZ();
    }

    public int getCurrentLeftBorder() {
        return Bukkit.getWorld("world").getWorldBorder().getCenter().getBlockX() - (((int) Bukkit.getWorld("world").getWorldBorder().getSize()) / 2);
    }

    public int getCurrentRightBorder() {
        return Bukkit.getWorld("world").getWorldBorder().getCenter().getBlockX() + (((int) Bukkit.getWorld("world").getWorldBorder().getSize()) / 2);
    }

    public int getCurrentLowerBorder() {
        return Bukkit.getWorld("world").getWorldBorder().getCenter().getBlockZ() - (((int) Bukkit.getWorld("world").getWorldBorder().getSize()) / 2);
    }

    public int getCurrentUpperBorder() {
        return Bukkit.getWorld("world").getWorldBorder().getCenter().getBlockZ() + (((int) Bukkit.getWorld("world").getWorldBorder().getSize()) / 2);
    }

    public double getCurrentZoneFloatDiameter() {
        return Bukkit.getWorld("world").getWorldBorder().getSize();
    }

    public double getCurrentZoneFloatCenterX() {
        return Bukkit.getWorld("world").getWorldBorder().getCenter().getX();
    }

    public double getCurrentZoneFloatCenterZ() {
        return Bukkit.getWorld("world").getWorldBorder().getCenter().getZ();
    }

    public double getCurrentLeftFloatBorder() {
        return Bukkit.getWorld("world").getWorldBorder().getCenter().getX() - ((Bukkit.getWorld("world").getWorldBorder().getSize()) / 2);
    }

    public double getCurrentRightFloatBorder() {
        return Bukkit.getWorld("world").getWorldBorder().getCenter().getX() + ((Bukkit.getWorld("world").getWorldBorder().getSize()) / 2);
    }

    public double getCurrentLowerFloatBorder() {
        return Bukkit.getWorld("world").getWorldBorder().getCenter().getZ() - ((Bukkit.getWorld("world").getWorldBorder().getSize()) / 2);
    }

    public double getCurrentUpperFloatBorder() {
        return Bukkit.getWorld("world").getWorldBorder().getCenter().getBlockZ() + ((Bukkit.getWorld("world").getWorldBorder().getSize()) / 2);
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
