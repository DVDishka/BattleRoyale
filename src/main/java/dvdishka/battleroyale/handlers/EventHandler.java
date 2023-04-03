package dvdishka.battleroyale.handlers;

import dvdishka.battleroyale.common.CommonVariables;
import dvdishka.battleroyale.common.ConfigVariables;
import dvdishka.battleroyale.tasks.BossBarTimerTask;
import dvdishka.battleroyale.tasks.NextZoneStageTask;
import io.papermc.paper.event.world.border.WorldBorderBoundsChangeFinishEvent;
import org.bukkit.Bukkit;
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
    public void onBorder(WorldBorderBoundsChangeFinishEvent event) {

        if (!event.getWorld().getName().equals("world") || !CommonVariables.isGameStarted) {

            return;
        }

        CommonVariables.logger.warning("1");

        // FINAL ZONE LOGIC
        if (CommonVariables.zoneStage == ConfigVariables.zones.size() - 1) {

            CommonVariables.finalZoneX = new Random().nextInt(-1 * ConfigVariables.zones.get(ConfigVariables.zones.size() - 1) / 2,
                    ConfigVariables.zones.get(ConfigVariables.zones.size() - 1) / 2);
            CommonVariables.finalZoneZ = new Random().nextInt(-1 * ConfigVariables.zones.get(ConfigVariables.zones.size() - 1) / 2,
                    ConfigVariables.zones.get(ConfigVariables.zones.size() - 1) / 2);

            for (World world : Bukkit.getWorlds()) {

                world.getWorldBorder().setCenter(CommonVariables.finalZoneX, CommonVariables.finalZoneZ);
                world.getWorldBorder().setSize(ConfigVariables.defaultWorldBorderDiametr);
            }

            Bukkit.getGlobalRegionScheduler().run(CommonVariables.plugin, (task -> {
                new BossBarTimerTask(CommonVariables.timer, ConfigVariables.finalZoneTimeOut, "Final zone - From " +
                        String.valueOf(CommonVariables.finalZoneX - ConfigVariables.finalZoneDiametr / 2) +
                        String.valueOf(CommonVariables.finalZoneZ - ConfigVariables.finalZoneDiametr / 2) +
                        " To " +
                        String.valueOf(CommonVariables.finalZoneX + ConfigVariables.finalZoneDiametr / 2) +
                        String.valueOf(CommonVariables.finalZoneZ + ConfigVariables.finalZoneDiametr / 2),
                        BarColor.GREEN).run();
            }));
            Bukkit.getGlobalRegionScheduler().runDelayed(CommonVariables.plugin, (task) -> {

                for (World world : Bukkit.getWorlds()) {

                   world.getWorldBorder().setSize(ConfigVariables.finalZoneDiametr);

                   if (!world.getName().equals("world")) {
                       for (Player player : world.getPlayers()) {
                           player.setHealth(0);
                       }
                   }
                }

                CommonVariables.isFinalZone = true;

                CommonVariables.timer.setColor(BarColor.RED);
                CommonVariables.timer.setProgress(1);
                CommonVariables.zoneStage++;
            }, (long) ConfigVariables.finalZoneTimeOut * 60 * 20);
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
                                    String.valueOf(ConfigVariables.zones.get(CommonVariables.zoneStage) / 2), BarColor.RED).run();

                    CommonVariables.zoneStage++;
                });

            } else {

                previousZone = ConfigVariables.zones.get(CommonVariables.zoneStage - 1);

                // TIMER TASK START
                Bukkit.getGlobalRegionScheduler().run(CommonVariables.plugin, (task -> {
                    new BossBarTimerTask(CommonVariables.timer, ConfigVariables.timeOut, "BREAK\nCurrent zone - " +
                            String.valueOf(previousZone / 2) +
                            "Next zone - " + String.valueOf(ConfigVariables.zones.get(CommonVariables.zoneStage) / 2),
                            BarColor.GREEN).run();
                }));

                // BOARDERS TASK START
                Bukkit.getGlobalRegionScheduler().runDelayed(CommonVariables.plugin, (task) -> {
                    new NextZoneStageTask().run();
                    new BossBarTimerTask(CommonVariables.timer, ConfigVariables.times.get(CommonVariables.zoneStage),
                            "Borders are going from " + String.valueOf(previousZone / 2) + " to " +
                                    String.valueOf(ConfigVariables.zones.get(CommonVariables.zoneStage) / 2), BarColor.RED).run();

                    CommonVariables.zoneStage++;
                }, timeOut * 60 * 20);
            }

            // SECOND ZONE LOGIC
            if (CommonVariables.zoneStage == 1) {

                for (World world : Bukkit.getWorlds()) {

                    world.setPVP(true);
                }
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
