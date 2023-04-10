package dvdishka.battleroyale.handlers;

import dvdishka.battleroyale.common.CommonVariables;
import dvdishka.battleroyale.common.ConfigVariables;
import dvdishka.battleroyale.common.UpdateEvent;
import dvdishka.battleroyale.tasks.BossBarTimerTask;
import dvdishka.battleroyale.tasks.NextZoneStageTask;
import dvdishka.battleroyale.tasks.ZoneMovingTask;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.Random;

public class EventHandler implements Listener {

    @org.bukkit.event.EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        CommonVariables.timer.addPlayer(event.getPlayer());
    }

    @org.bukkit.event.EventHandler
    public void onBorder(UpdateEvent event) {

        CommonVariables.logger.warning("1");
        Bukkit.getGlobalRegionScheduler().cancelTasks(CommonVariables.plugin);

        // ZONE MOVING LOGIC
        if (CommonVariables.zoneStage == ConfigVariables.zones.size()) {

            int side = new Random().nextInt(0, 4);
            int length = new Random().nextInt(ConfigVariables.minFinalZoneMove, ConfigVariables.maxFinalZoneMove) / 10 * 10;
            String sideName;
            int x, z;

            switch (side) {

                // East
                case 0:
                    x = 1;
                    z = 0;
                    sideName = "East";
                    break;

                // WEST
                case 1:
                    x = -1;
                    z = 0;
                    sideName = "West";
                    break;

                // SOUTH
                case 2:
                    z = 1;
                    x = 0;
                    sideName = "South";
                    break;

                // NORTH
                case 3:
                    z = -1;
                    x = 0;
                    sideName = "North";
                    break;

                default:
                    x = 0;
                    z = 0;
                    sideName = "";
                    break;
            }
            Bukkit.getGlobalRegionScheduler().run(CommonVariables.plugin, (task -> {
                new BossBarTimerTask(CommonVariables.timer, ConfigVariables.zoneMoveTimeout, "Break zone will move " + sideName, BarColor.GREEN, false).run();
            }));

            Bukkit.getGlobalRegionScheduler().runDelayed(CommonVariables.plugin, (task -> {

                new BossBarTimerTask(CommonVariables.timer, ConfigVariables.finalZoneMoveDuration, "Zone is moving " + sideName, BarColor.RED, true).run();
                new ZoneMovingTask(x, z, ConfigVariables.finalZoneMoveDuration, Math.abs(length)).run();
            }), ConfigVariables.zoneMoveTimeout * 20L);
        }

        // FINAL ZONE LOGIC
        if (CommonVariables.zoneStage == ConfigVariables.zones.size() - 1) {

            CommonVariables.finalZoneX = new Random().nextInt(-1 * ConfigVariables.zones.get(ConfigVariables.zones.size() - 1) / 2,
                    ConfigVariables.zones.get(ConfigVariables.zones.size() - 1) / 2);
            CommonVariables.finalZoneZ = new Random().nextInt(-1 * ConfigVariables.zones.get(ConfigVariables.zones.size() - 1) / 2,
                    ConfigVariables.zones.get(ConfigVariables.zones.size() - 1) / 2);

            Bukkit.getGlobalRegionScheduler().run(CommonVariables.plugin, (task -> {
                new BossBarTimerTask(CommonVariables.timer, ConfigVariables.finalZoneTimeOut, "Final zone - From " +
                        String.valueOf(CommonVariables.finalZoneX - ConfigVariables.finalZoneDiametr / 2) +
                        String.valueOf(CommonVariables.finalZoneZ - ConfigVariables.finalZoneDiametr / 2) +
                        " To " +
                        String.valueOf(CommonVariables.finalZoneX + ConfigVariables.finalZoneDiametr / 2) +
                        String.valueOf(CommonVariables.finalZoneZ + ConfigVariables.finalZoneDiametr / 2),
                        BarColor.GREEN, false).run();
            }));
            Bukkit.getGlobalRegionScheduler().runDelayed(CommonVariables.plugin, (task) -> {

                for (World world : Bukkit.getWorlds()) {

                    world.getWorldBorder().setCenter(CommonVariables.finalZoneX, CommonVariables.finalZoneZ);
                    world.getWorldBorder().setSize(ConfigVariables.finalZoneDiametr);

                   if (!world.getName().equals("world")) {
                       for (Player player : world.getPlayers()) {
                           EntityScheduler playerScheduler = player.getScheduler();
                           playerScheduler.run(CommonVariables.plugin, (playerSchedulerTask) -> {
                               player.setHealth(0);
                           }, null);
                       }
                   }
                }

                new BossBarTimerTask(CommonVariables.timer, ConfigVariables.finalZoneDuration, "Final zone", BarColor.RED, true).run();

                CommonVariables.isFinalZone = true;
                CommonVariables.isZoneMove = true;

                CommonVariables.zoneStage++;
            }, (long) ConfigVariables.finalZoneTimeOut * 20);
        }

        // NEW ZONE START LOGIC
        if (CommonVariables.zoneStage < ConfigVariables.zones.size() - 1) {

            long timeOut = ConfigVariables.timeOut;
            int previousZone;

            // FIRST ZONE LOGIC
            if (CommonVariables.zoneStage == 0) {

                previousZone = ConfigVariables.defaultWorldBorderDiametr;

                // BOARDERS TASK START
                Bukkit.getGlobalRegionScheduler().run(CommonVariables.plugin, (task) -> {
                    new NextZoneStageTask().run();
                    new BossBarTimerTask(CommonVariables.timer, ConfigVariables.times.get(CommonVariables.zoneStage),
                            "Borders are going from " + String.valueOf(previousZone / 2) + " to " +
                                    String.valueOf(ConfigVariables.zones.get(CommonVariables.zoneStage) / 2), BarColor.RED, true).run();

                    CommonVariables.zoneStage++;
                });

            } else {

                previousZone = ConfigVariables.zones.get(CommonVariables.zoneStage - 1);

                // TIMER TASK START
                Bukkit.getGlobalRegionScheduler().run(CommonVariables.plugin, (task -> {
                    new BossBarTimerTask(CommonVariables.timer, ConfigVariables.timeOut, "BREAK\nCurrent zone - " +
                            String.valueOf(previousZone / 2) +
                            "Next zone - " + String.valueOf(ConfigVariables.zones.get(CommonVariables.zoneStage) / 2),
                            BarColor.GREEN, false).run();
                }));

                // BOARDERS TASK START
                Bukkit.getGlobalRegionScheduler().runDelayed(CommonVariables.plugin, (task) -> {

                    // SECOND ZONE LOGIC
                    if (CommonVariables.zoneStage == 1) {

                        for (World world : Bukkit.getWorlds()) {

                            world.setPVP(true);
                        }

                        for (Player player : Bukkit.getOnlinePlayers()) {

                            player.sendTitle(ChatColor.RED + "PVP is now enabled!", "");
                        }
                    }

                    new NextZoneStageTask().run();
                    new BossBarTimerTask(CommonVariables.timer, ConfigVariables.times.get(CommonVariables.zoneStage),
                            "Borders are going from " + String.valueOf(previousZone / 2) + " to " +
                                    String.valueOf(ConfigVariables.zones.get(CommonVariables.zoneStage) / 2), BarColor.RED, true).run();

                    CommonVariables.zoneStage++;
                }, timeOut * 20);
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onPortal(PlayerPortalEvent event) {

        if (CommonVariables.isFinalZone) {

            event.setCancelled(true);
        }
    }
}
