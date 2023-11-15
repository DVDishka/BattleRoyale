package ru.dvdishka.battleroyale.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import ru.dvdishka.battleroyale.logic.event.NextGameStageEvent;
import ru.dvdishka.battleroyale.logic.Zone;
import ru.dvdishka.battleroyale.logic.classes.ZonePhase;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.ConfigVariables;
import ru.dvdishka.battleroyale.logic.Scheduler;
import ru.dvdishka.battleroyale.ui.Radar;
import ru.dvdishka.battleroyale.ui.Timer;

import java.util.Random;

public class ZoneStageHandler implements Listener  {

    @org.bukkit.event.EventHandler
    public void onNextGameStageEvent(NextGameStageEvent event) {

        if (!Common.isGameStarted) {
            return;
        }

        if (Common.zoneStage < ConfigVariables.lastReviveZone) {
            Common.zoneStage++;
        }

        // REVIVE DISABLE LOGIC
        if (Common.zoneStage == ConfigVariables.lastReviveZone) {

            Common.isRevivalEnabled = false;

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(ChatColor.RED + "Revival", ChatColor.RED + "Is now disabled!");
            }
        }

        // PVP ENABLE LOGIC
        if (Common.zoneStage == ConfigVariables.pvpEnableZone) {

            Common.isPVPEnabled = true;

            for (World world : Bukkit.getWorlds()) {
                world.setPVP(true);
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(ChatColor.RED + "PVP", ChatColor.RED + "Is now enabled!");
            }
        }

        if (Common.zoneStage == 0) {
            firstZoneStage();
        }

        // ZONE MOVING STAGE LOGIC
        else if (Common.zoneStage == ConfigVariables.zones.size()) {
            zoneMovingStage();
        }

        // NEW ZONE START LOGIC
        else if (Common.zoneStage < ConfigVariables.zones.size()) {
            mainNextStageLogic();
        }
    }

    private void firstZoneStage() {

        int timeOut = ConfigVariables.timeOuts.get(0);
        final int oldZoneCenterX = Bukkit.getWorld("world").getWorldBorder().getCenter().getBlockX();
        final int oldZoneCenterZ = Bukkit.getWorld("world").getWorldBorder().getCenter().getBlockZ();
        int previousZoneDiameter = ConfigVariables.defaultWorldBorderDiameter;

        final int nextZoneCenterX = Zone.getInstance().generateRandomZoneCenterX(
                ConfigVariables.defaultWorldBorderDiameter / 2,
                ConfigVariables.zones.get(Common.zoneStage) / 2,
                oldZoneCenterX);
        final int nextZoneCenterZ = Zone.getInstance().generateRandomZoneCenterZ(
                ConfigVariables.defaultWorldBorderDiameter / 2,
                ConfigVariables.zones.get(Common.zoneStage) / 2,
                oldZoneCenterZ);

        Zone.getInstance().setVariables(ConfigVariables.defaultWorldBorderDiameter,
                ConfigVariables.zones.get(Common.zoneStage),
                oldZoneCenterX,
                oldZoneCenterZ,
                nextZoneCenterX,
                nextZoneCenterZ);

        // BREAK BOSS BAR TIMER TASK START
        Scheduler.getScheduler().runSync(Common.plugin, (scheduledTask) -> {
            Common.isBreak = true;
            Timer.getInstance().startTimer(timeOut, ZonePhase.BREAK, false);
        });

        // ACTIVE BORDERS TASK START
        Scheduler.getScheduler().runSyncDelayed(Common.plugin, (scheduledTask) -> {

            Common.isBreak = false;

            // CHANGE WORLD BORDER
            Zone.getInstance().changeBorders(
                    ConfigVariables.defaultWorldBorderDiameter,
                    ConfigVariables.zones.get(Common.zoneStage),
                    ConfigVariables.times.get(Common.zoneStage),
                    oldZoneCenterX,
                    oldZoneCenterZ,
                    nextZoneCenterX,
                    nextZoneCenterZ);

            // ACTIVE BOSS BAR TIMER TASK START
            Timer.getInstance().startTimer(ConfigVariables.times.get(Common.zoneStage), ZonePhase.ACTIVE, true);
            }, timeOut * 20L);
    }

    public void mainNextStageLogic() {

        int timeOut = ConfigVariables.timeOuts.get(Common.zoneStage + 1);
        int previousZoneDiameter = ConfigVariables.zones.get(Common.zoneStage - 1);

        final int oldZoneCenterX = Zone.getInstance().getNewZoneCenterX();
        final int oldZoneCenterZ = Zone.getInstance().getNewZoneCenterZ();

        final int nextZoneCenterX = Zone.getInstance().generateRandomZoneCenterX(
                previousZoneDiameter / 2,
                ConfigVariables.zones.get(Common.zoneStage) / 2,
                oldZoneCenterX);
        final int nextZoneCenterZ = Zone.getInstance().generateRandomZoneCenterZ(
                previousZoneDiameter / 2,
                ConfigVariables.zones.get(Common.zoneStage) / 2,
                oldZoneCenterZ);

        Zone.getInstance().setVariables(ConfigVariables.zones.get(Common.zoneStage - 1),
                ConfigVariables.zones.get(Common.zoneStage),
                oldZoneCenterX,
                oldZoneCenterZ,
                nextZoneCenterX,
                nextZoneCenterZ);

        // BREAK BOSS BAR TIMER TASK START
        Scheduler.getScheduler().runSync(Common.plugin, (scheduledTask) -> {

            Common.isBreak = true;

            Timer.getInstance().startTimer(timeOut, ZonePhase.BREAK, false);
        });

        // ACTIVE BORDERS TASK START
        Scheduler.getScheduler().runSyncDelayed(Common.plugin, (scheduledTask) -> {

            Common.isBreak = false;

            // CHANGE WORLD BORDER
            Zone.getInstance().changeBorders(
                    ConfigVariables.zones.get(Common.zoneStage - 1),
                    ConfigVariables.zones.get(Common.zoneStage),
                    ConfigVariables.times.get(Common.zoneStage),
                    oldZoneCenterX,
                    oldZoneCenterZ,
                    nextZoneCenterX,
                    nextZoneCenterZ);

            // ACTIVE BOSS BAR TIMER TASK START
            Timer.getInstance().startTimer(ConfigVariables.times.get(Common.zoneStage), ZonePhase.ACTIVE, true);
            }, timeOut * 20L);
    }

    private void zoneMovingStage() {

        // KILL PLAYER IF NOT IN OVERWORLD AND LOCK PORTALS
        for (World world : Bukkit.getWorlds()) {

            Common.isPortalLocked = true;

            if (!world.getName().equals("world")) {
                for (Player player : world.getPlayers()) {
                    Scheduler.getScheduler().runPlayerTask(Common.plugin, player, (scheduledTask) -> player.setHealth(0));
                }
            }
        }

        int side = new Random().nextInt(0, 4);
        int moveLength = new Random().nextInt(ConfigVariables.minFinalZoneMove, ConfigVariables.maxFinalZoneMove + 1) / 10 * 10;
        String sideName;
        int x, z;

        switch (side) {

            // East
            case 0 -> {
                Radar.getInstance().movingZoneChar = ">";
                x = 1;
                z = 0;
                sideName = "East";
            }

            // WEST
            case 1 -> {
                Radar.getInstance().movingZoneChar = "<";
                x = -1;
                z = 0;
                sideName = "West";
            }

            // SOUTH
            case 2 -> {
                Radar.getInstance().movingZoneChar = "V";
                z = 1;
                x = 0;
                sideName = "South";
            }

            // NORTH
            case 3 -> {
                Radar.getInstance().movingZoneChar = "A";
                z = -1;
                x = 0;
                sideName = "North";
            }

            default -> {
                Radar.getInstance().movingZoneChar = "=";
                x = 0;
                z = 0;
                sideName = "";
            }
        }

        Zone.getInstance().setVariables(
                ConfigVariables.zones.get(ConfigVariables.zones.size() - 1),
                ConfigVariables.zones.get(ConfigVariables.zones.size() - 1),
                Zone.getInstance().getNewZoneCenterX(),
                Zone.getInstance().getNewZoneCenterZ(),
                Zone.getInstance().getNewZoneCenterX() + x * moveLength,
                Zone.getInstance().getNewZoneCenterZ() + z * moveLength);

        // BREAK BOSS BAR TIMER TASK START
        Scheduler.getScheduler().runSync(Common.plugin, (scheduledTask) -> {
            Common.isBreak = true;
            Timer.getInstance().startTimer(ConfigVariables.zoneMoveTimeOut, ZonePhase.BREAK, false);
        });

        // MOVE BORDERS MOVING TASK START
        Scheduler.getScheduler().runSyncDelayed(Common.plugin, (scheduledTask) -> {
            Common.isBreak = false;
            Timer.getInstance().startTimer(ConfigVariables.finalZoneMoveDuration, ZonePhase.MOVE, true);
            Zone.getInstance().moveZone(x, z, ConfigVariables.finalZoneMoveDuration, Math.abs(moveLength));
        }, ConfigVariables.zoneMoveTimeOut * 20L);
    }
}
