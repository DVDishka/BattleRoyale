package ru.dvdishka.battleroyale.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import ru.dvdishka.battleroyale.classes.NextGameStageEvent;
import ru.dvdishka.battleroyale.common.Common;
import ru.dvdishka.battleroyale.common.ConfigVariables;
import ru.dvdishka.battleroyale.common.Scheduler;
import ru.dvdishka.battleroyale.tasks.BossBarTimerTask;
import ru.dvdishka.battleroyale.tasks.ZoneMovingTask;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ZonesHandler implements Listener  {

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

        // FINAL ZONE LOGIC
        else if (Common.zoneStage == ConfigVariables.zones.size() - 1) {
            finalZoneStage();
        }

        // NEW ZONE START LOGIC
        else if (Common.zoneStage < ConfigVariables.zones.size() - 1) {
            mainNextStageLogic();
        }
    }

    private void firstZoneStage() {

        int previousZoneDiameter = ConfigVariables.defaultWorldBorderDiameter;

        // ACTIVE BOARDERS TASK START
        Scheduler.getScheduler().runSync(Common.plugin, () -> {

            // CHANGE WORLD BOARDER
            for (World world : Bukkit.getWorlds()) {
                world.getWorldBorder().setSize(ConfigVariables.zones.get(Common.zoneStage),
                        TimeUnit.SECONDS, ConfigVariables.times.get(Common.zoneStage));
            }

            // ACTIVE BOSS BAR TIMER TASK START
            new BossBarTimerTask(Common.timer, ConfigVariables.times.get(Common.zoneStage),
                    ChatColor.RED + "Zone moves From " + String.valueOf(previousZoneDiameter / 2) + " To " +
                            String.valueOf(ConfigVariables.zones.get(Common.zoneStage) / 2), BarColor.RED, true).run();

            Common.zoneStage++;
        });
    }

    public void mainNextStageLogic() {

        long timeOut = ConfigVariables.timeOut;
        int previousZoneDiameter = ConfigVariables.zones.get(Common.zoneStage - 1);

        // BREAK BOSS BAR TIMER TASK START
        Scheduler.getScheduler().runSync(Common.plugin, () -> {
            new BossBarTimerTask(Common.timer, ConfigVariables.timeOut, ChatColor.YELLOW +
                    "Current zone - " +
                    String.valueOf(previousZoneDiameter / 2) +
                    " ! Next zone - " + String.valueOf(ConfigVariables.zones.get(Common.zoneStage) / 2),
                    BarColor.GREEN, false).run();
        });

        // ACTIVE BOARDERS TASK START
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

            // CHANGE WORLD BOARDER
            for (World world : Bukkit.getWorlds()) {
                world.getWorldBorder().setSize(ConfigVariables.zones.get(Common.zoneStage),
                        TimeUnit.SECONDS, ConfigVariables.times.get(Common.zoneStage));
            }

            // ACTIVE BOSS BAR TIMER TASK START
            new BossBarTimerTask(Common.timer, ConfigVariables.times.get(Common.zoneStage),
                    ChatColor.RED + "Zone moves From " + String.valueOf(previousZoneDiameter / 2) + " To " +
                            String.valueOf(ConfigVariables.zones.get(Common.zoneStage) / 2), BarColor.RED, true).run();

            Common.zoneStage++;
        }, timeOut * 20);
    }

    private void finalZoneStage() {

        // RANDOMIZE FINAL ZONE CENTER
        Common.finalZoneX = new Random().nextInt(-1 * ConfigVariables.zones.get(ConfigVariables.zones.size() - 1) / 2,
                ConfigVariables.zones.get(ConfigVariables.zones.size() - 1) / 2);
        Common.finalZoneZ = new Random().nextInt(-1 * ConfigVariables.zones.get(ConfigVariables.zones.size() - 1) / 2,
                ConfigVariables.zones.get(ConfigVariables.zones.size() - 1) / 2);

        // BREAK BOSS BAR TIMER TASK START
        Scheduler.getScheduler().runSync(Common.plugin, () -> {
            new BossBarTimerTask(Common.timer, ConfigVariables.finalZoneTimeOut, ChatColor.DARK_PURPLE +
                    "BREAK! Final zone - From " +
                    String.valueOf(Common.finalZoneX - ConfigVariables.finalZoneDiameter / 2) + " " +
                    String.valueOf(Common.finalZoneZ - ConfigVariables.finalZoneDiameter / 2) +
                    " To " +
                    String.valueOf(Common.finalZoneX + ConfigVariables.finalZoneDiameter / 2) + " " +
                    String.valueOf(Common.finalZoneZ + ConfigVariables.finalZoneDiameter / 2),
                    BarColor.GREEN, false).run();
        });

        // CHANGE CENTER OF WORLD BOARDER
        for (World world : Bukkit.getWorlds()) {
            world.getWorldBorder().setSize(ConfigVariables.zones.get(ConfigVariables.zones.size() - 1) * 4);
            world.getWorldBorder().setCenter(Common.finalZoneX, Common.finalZoneZ);
        }

        // ACTIVE FINAL ZONE LOGIC
        Scheduler.getScheduler().runSyncDelayed(Common.plugin, () -> {

            for (World world : Bukkit.getWorlds()) {

                world.getWorldBorder().setSize(ConfigVariables.finalZoneDiameter, ConfigVariables.finalZoneDuration);

                // KILL PLAYER IF NOT IN OVERWORLD
                if (!world.getName().equals("world")) {
                    for (Player player : world.getPlayers()) {
                        Scheduler.getScheduler().runPlayerTask(Common.plugin, player, () -> {
                            player.setHealth(0);
                        });
                    }
                }
            }

            // ACTIVE BOSS BAR TIMER TASK START
            new BossBarTimerTask(Common.timer, ConfigVariables.finalZoneDuration, ChatColor.RED +
                    "Final zone - From " +
                    String.valueOf(Common.finalZoneX - ConfigVariables.finalZoneDiameter / 2) + " " +
                    String.valueOf(Common.finalZoneZ - ConfigVariables.finalZoneDiameter / 2) +
                    " To " +
                    String.valueOf(Common.finalZoneX + ConfigVariables.finalZoneDiameter / 2) + " " +
                    String.valueOf(Common.finalZoneZ + ConfigVariables.finalZoneDiameter / 2),
                    BarColor.RED, true).run();

            Common.isFinalZone = true;
            Common.isZoneMove = true;

            Common.zoneStage++;
        }, (long) ConfigVariables.finalZoneTimeOut * 20);
    }

    private void zoneMovingStage() {

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

        // ACTIVE BOARDERS MOVING TASK START
        Scheduler.getScheduler().runSyncDelayed(Common.plugin, () -> {
            new BossBarTimerTask(Common.timer, ConfigVariables.finalZoneMoveDuration, ChatColor.RED + "The zone moves " + sideName, BarColor.RED, true).run();
            new ZoneMovingTask(x, z, ConfigVariables.finalZoneMoveDuration, Math.abs(moveLength)).run();
        }, ConfigVariables.zoneMoveTimeOut * 20L);
    }
}
