package ru.dvdishka.battleroyale.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import ru.dvdishka.battleroyale.classes.NextGameStageEvent;
import ru.dvdishka.battleroyale.classes.Zone;
import ru.dvdishka.battleroyale.common.Common;
import ru.dvdishka.battleroyale.common.ConfigVariables;
import ru.dvdishka.battleroyale.common.Scheduler;
import ru.dvdishka.battleroyale.tasks.BossBarTimerTask;

import java.util.Random;

public class ZoneStageHandler implements Listener  {

    @org.bukkit.event.EventHandler
    public void onNextGameStageEvent(NextGameStageEvent event) {

        if (!Common.isGameStarted) {
            return;
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

        final int oldZoneCenterX = Bukkit.getWorld("world").getWorldBorder().getCenter().getBlockX();
        final int oldZoneCenterZ = Bukkit.getWorld("world").getWorldBorder().getCenter().getBlockZ();

        final int nextZoneCenterX = Zone.getInstance().generateRandomZoneCenterX(
                ConfigVariables.defaultWorldBorderDiameter / 2,
                ConfigVariables.zones.get(Common.zoneStage) / 2,
                oldZoneCenterX);
        final int nextZoneCenterZ = Zone.getInstance().generateRandomZoneCenterZ(
                ConfigVariables.defaultWorldBorderDiameter / 2,
                ConfigVariables.zones.get(Common.zoneStage) / 2,
                oldZoneCenterZ);

        // ACTIVE BORDERS TASK START
        Scheduler.getScheduler().runSync(Common.plugin, () -> {

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
            new BossBarTimerTask(Common.timer, ConfigVariables.times.get(Common.zoneStage),
                    ChatColor.RED + "Zone moves From " + String.valueOf(ConfigVariables.defaultWorldBorderDiameter / 2) + " To " +
                            String.valueOf(ConfigVariables.zones.get(Common.zoneStage) / 2), BarColor.RED, true).run();

            Common.zoneStage++;
        });
    }

    public void mainNextStageLogic() {

        int timeOut = ConfigVariables.timeOut;
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
        Scheduler.getScheduler().runSync(Common.plugin, () -> {
            new BossBarTimerTask(Common.timer, timeOut, ChatColor.YELLOW +
                    "Current zone - " +
                    String.valueOf(previousZoneDiameter / 2) +
                    " ! Next zone - " + String.valueOf(ConfigVariables.zones.get(Common.zoneStage) / 2),
                    BarColor.GREEN, false).run();
        });

        // ACTIVE BORDERS TASK START
        Scheduler.getScheduler().runSyncDelayed(Common.plugin, () -> {

            // PVP ENABLE LOGIC
            if (Common.zoneStage == 1) {

                for (World world : Bukkit.getWorlds()) {
                    world.setPVP(true);
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendTitle(ChatColor.RED + "PVP is now enabled!", "");
                }
            }

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
            new BossBarTimerTask(Common.timer, ConfigVariables.times.get(Common.zoneStage),
                    ChatColor.RED + "Zone moves From " + String.valueOf(previousZoneDiameter / 2) + " To " +
                            String.valueOf(ConfigVariables.zones.get(Common.zoneStage) / 2), BarColor.RED, true).run();

            Common.zoneStage++;
        }, timeOut * 20L);
    }

    private void zoneMovingStage() {

        // KILL PLAYER IF NOT IN OVERWORLD AND LOCK PORTALS
        for (World world : Bukkit.getWorlds()) {

            Common.isPortalLocked = true;

            if (!world.getName().equals("world")) {
                for (Player player : world.getPlayers()) {
                    Scheduler.getScheduler().runPlayerTask(Common.plugin, player, () -> player.setHealth(0));
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
                x = 1;
                z = 0;
                sideName = "East";
            }

            // WEST
            case 1 -> {
                x = -1;
                z = 0;
                sideName = "West";
            }

            // SOUTH
            case 2 -> {
                z = 1;
                x = 0;
                sideName = "South";
            }

            // NORTH
            case 3 -> {
                z = -1;
                x = 0;
                sideName = "North";
            }

            default -> {
                x = 0;
                z = 0;
                sideName = "";
            }
        }

        // BREAK BOSS BAR TIMER TASK START
        Scheduler.getScheduler().runSync(Common.plugin, () -> {
            new BossBarTimerTask(Common.timer, ConfigVariables.zoneMoveTimeOut, ChatColor.YELLOW + "BREAK! The zone will move " + sideName, BarColor.GREEN, false).run();
        });

        // ACTIVE BORDERS MOVING TASK START
        Scheduler.getScheduler().runSyncDelayed(Common.plugin, () -> {
            new BossBarTimerTask(Common.timer, ConfigVariables.finalZoneMoveDuration, ChatColor.RED + "The zone moves " + sideName, BarColor.RED, true).run();
            Zone.getInstance().moveZone(x, z, ConfigVariables.finalZoneMoveDuration, Math.abs(moveLength));
        }, ConfigVariables.zoneMoveTimeOut * 20L);
    }
}
