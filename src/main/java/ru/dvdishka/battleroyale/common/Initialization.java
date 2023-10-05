package ru.dvdishka.battleroyale.common;

import org.bukkit.configuration.file.FileConfiguration;

public class Initialization {

    public static void checkDependencies() {

        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
            CommonVariables.isFolia = true;
            Logger.getLogger().devLog("Paper/Folia has been detected!");
        } catch (Exception e) {
            CommonVariables.isFolia = false;
            Logger.getLogger().devLog("Paper/Folia has not been detected!");
        }
    }

    public static void initConfig() {

        FileConfiguration config = CommonVariables.plugin.getConfig();

        ConfigVariables.defaultWorldBorderDiameter = config.getInt("defaultWorldBorderDiameter");
        ConfigVariables.zones = config.getIntegerList("zones");
        ConfigVariables.times = config.getIntegerList("times");
        ConfigVariables.timeOut = config.getInt("timeOut");
        ConfigVariables.finalZoneDuration = config.getInt("finalZoneDuration");
        ConfigVariables.finalZoneDiameter = config.getInt("finalZoneDiameter");
        ConfigVariables.finalZoneTimeOut = config.getInt("finalZoneTimeOut");
        ConfigVariables.finalZoneMoveDuration = config.getInt("finalZoneMoveDuration");
        ConfigVariables.minFinalZoneMove = config.getInt("minFinalZoneMove");
        ConfigVariables.maxFinalZoneMove = config.getInt("minFinalZoneMove");
        ConfigVariables.zoneMoveTimeOut = config.getInt("zoneMoveTimeOut");
        ConfigVariables.startBoxX = config.getInt("startBoxX");
        ConfigVariables.startBoxY = config.getInt("startBoxY");
        ConfigVariables.startBoxZ = config.getInt("startBoxZ");
        ConfigVariables.betterLogging = config.getBoolean("betterLogging");
    }
}
