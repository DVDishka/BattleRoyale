package dvdishka.battleroyale.handlers;

import dvdishka.battleroyale.classes.*;
import dvdishka.battleroyale.common.CommonVariables;
import dvdishka.battleroyale.common.ConfigVariables;
import dvdishka.battleroyale.common.Scheduler;
import dvdishka.battleroyale.tasks.BossBarTimerTask;
import dvdishka.battleroyale.tasks.NextZoneStageTask;
import dvdishka.battleroyale.tasks.ZoneMovingTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Random;

public class EventHandler implements Listener {

    @org.bukkit.event.EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (CommonVariables.isGameStarted) {
            if (CommonVariables.deadPlayers.contains(event.getPlayer().getName()) &&
                    Team.getTeam(event.getPlayer()) != null &&
                    !CommonVariables.deadTeams.contains(Team.getTeam(event.getPlayer()).getName())) {

                Scheduler.getScheduler().runPlayerTask(CommonVariables.plugin, event.getPlayer(), () -> {
                    event.getPlayer().kick(Component.text("You are out and your team is not yet!"));
                });
            }
        }

        CommonVariables.timer.addPlayer(event.getPlayer());

        Player player = event.getPlayer();

        Scheduler.getScheduler().runPlayerTask(CommonVariables.plugin, player, () -> {

            if (!CommonVariables.players.contains(player.getName()) && CommonVariables.isGameStarted) {

                player.setGameMode(GameMode.SURVIVAL);

                ArrayList<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());
                for (PotionEffect effect : effects) {
                    player.removePotionEffect(effect.getType());
                }

                int powerNumber = new Random().nextInt(0, SuperPower.values().length);
                SuperPower.values()[powerNumber].setToPlayer(player);
            }
        });
    }

    @org.bukkit.event.EventHandler
    public void onBorder(UpdateEvent event) {

        if (!CommonVariables.isGameStarted) {

            return;
        }

        // ZONE MOVING LOGIC
        if (CommonVariables.zoneStage == ConfigVariables.zones.size()) {

            int side = new Random().nextInt(0, 4);
            int length = new Random().nextInt(ConfigVariables.minFinalZoneMove, ConfigVariables.maxFinalZoneMove) / 10 * 10;
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
            Scheduler.getScheduler().runSync(CommonVariables.plugin, () -> {
                new BossBarTimerTask(CommonVariables.timer, ConfigVariables.zoneMoveTimeout, ChatColor.YELLOW + "BREAK! The zone will move " + sideName, BarColor.GREEN, false).run();
            });

            Scheduler.getScheduler().runSyncDelayed(CommonVariables.plugin, () -> {
                new BossBarTimerTask(CommonVariables.timer, ConfigVariables.finalZoneMoveDuration, ChatColor.RED + "The zone moves " + sideName, BarColor.RED, true).run();
                new ZoneMovingTask(x, z, ConfigVariables.finalZoneMoveDuration, Math.abs(length)).run();
            }, ConfigVariables.zoneMoveTimeout * 20L);
        }

        // FINAL ZONE LOGIC
        if (CommonVariables.zoneStage == ConfigVariables.zones.size() - 1) {

            CommonVariables.finalZoneX = new Random().nextInt(-1 * ConfigVariables.zones.get(ConfigVariables.zones.size() - 1) / 2,
                    ConfigVariables.zones.get(ConfigVariables.zones.size() - 1) / 2);
            CommonVariables.finalZoneZ = new Random().nextInt(-1 * ConfigVariables.zones.get(ConfigVariables.zones.size() - 1) / 2,
                    ConfigVariables.zones.get(ConfigVariables.zones.size() - 1) / 2);

            Scheduler.getScheduler().runSync(CommonVariables.plugin, () -> {
                new BossBarTimerTask(CommonVariables.timer, ConfigVariables.finalZoneTimeOut, ChatColor.DARK_PURPLE +
                        "BREAK! Final zone - From " +
                        String.valueOf(CommonVariables.finalZoneX - ConfigVariables.finalZoneDiameter / 2) + " " +
                        String.valueOf(CommonVariables.finalZoneZ - ConfigVariables.finalZoneDiameter / 2) +
                        " To " +
                        String.valueOf(CommonVariables.finalZoneX + ConfigVariables.finalZoneDiameter / 2) + " " +
                        String.valueOf(CommonVariables.finalZoneZ + ConfigVariables.finalZoneDiameter / 2),
                        BarColor.GREEN, false).run();
            });
            for (World world : Bukkit.getWorlds()) {

                world.getWorldBorder().setSize(ConfigVariables.zones.get(ConfigVariables.zones.size() - 1) * 4);
                world.getWorldBorder().setCenter(CommonVariables.finalZoneX, CommonVariables.finalZoneZ);
            }
            Scheduler.getScheduler().runSyncDelayed(CommonVariables.plugin, () -> {

                for (World world : Bukkit.getWorlds()) {

                    world.getWorldBorder().setSize(ConfigVariables.finalZoneDiameter, ConfigVariables.finalZoneDuration);

                   if (!world.getName().equals("world")) {
                       for (Player player : world.getPlayers()) {
                           Scheduler.getScheduler().runPlayerTask(CommonVariables.plugin, player, () -> {
                               player.setHealth(0);
                           });
                       }
                   }
                }

                new BossBarTimerTask(CommonVariables.timer, ConfigVariables.finalZoneDuration, ChatColor.RED +
                        "Final zone - From " +
                        String.valueOf(CommonVariables.finalZoneX - ConfigVariables.finalZoneDiameter / 2) + " " +
                        String.valueOf(CommonVariables.finalZoneZ - ConfigVariables.finalZoneDiameter / 2) +
                        " To " +
                        String.valueOf(CommonVariables.finalZoneX + ConfigVariables.finalZoneDiameter / 2) + " " +
                        String.valueOf(CommonVariables.finalZoneZ + ConfigVariables.finalZoneDiameter / 2),
                        BarColor.RED, true).run();

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

                previousZone = ConfigVariables.defaultWorldBorderDiameter;

                // BOARDERS TASK START
                Scheduler.getScheduler().runSync(CommonVariables.plugin, () -> {
                    new NextZoneStageTask().run();
                    new BossBarTimerTask(CommonVariables.timer, ConfigVariables.times.get(CommonVariables.zoneStage),
                            ChatColor.RED + "Zone moves From " + String.valueOf(previousZone / 2) + " To " +
                                    String.valueOf(ConfigVariables.zones.get(CommonVariables.zoneStage) / 2), BarColor.RED, true).run();

                    CommonVariables.zoneStage++;
                });

            } else {

                previousZone = ConfigVariables.zones.get(CommonVariables.zoneStage - 1);

                // TIMER TASK START
                Scheduler.getScheduler().runSync(CommonVariables.plugin, () -> {
                    new BossBarTimerTask(CommonVariables.timer, ConfigVariables.timeOut, ChatColor.YELLOW +
                            "Current zone - " +
                            String.valueOf(previousZone / 2) +
                            " ! Next zone - " + String.valueOf(ConfigVariables.zones.get(CommonVariables.zoneStage) / 2),
                            BarColor.GREEN, false).run();
                });

                // BOARDERS TASK START
                Scheduler.getScheduler().runSyncDelayed(CommonVariables.plugin, () -> {

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
                            ChatColor.RED + "Zone moves From " + String.valueOf(previousZone / 2) + " To " +
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

    @org.bukkit.event.EventHandler
    public void onQuit(PlayerQuitEvent event) {

        ArrayList<String> aliveTeams = new ArrayList<>();
        Player player = event.getPlayer();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            if (onlinePlayer.getGameMode().equals(GameMode.SURVIVAL) && !onlinePlayer.getName().equals(player.getName())) {
                if (Team.getTeam(onlinePlayer) != null) {
                    if (!aliveTeams.contains(onlinePlayer.getName())) {
                        aliveTeams.add(Team.getTeam(onlinePlayer).getName());
                    }
                } else {
                    aliveTeams.add(onlinePlayer.getName());
                }
            }
        }

        Team playerTeam = Team.getTeam(player);

        if (aliveTeams.size() == 1) {

            if (playerTeam == null || !aliveTeams.contains(playerTeam.getName())) {

                Scheduler.getScheduler().runPlayerTask(CommonVariables.plugin, player, () -> {
                    player.setGameMode(GameMode.SPECTATOR);
                });

                CommonVariables.deadPlayers.add(player.getName());

                if (playerTeam != null) {
                    CommonVariables.deadTeams.add(playerTeam.getName());
                }

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                    if (playerTeam != null) {
                        Scheduler.getScheduler().runPlayerTask(CommonVariables.plugin, onlinePlayer, () -> {
                            onlinePlayer.sendTitle(ChatColor.RED + "Team " + playerTeam.getName() + " is eliminated!", "");
                        });
                    } else {
                        Scheduler.getScheduler().runPlayerTask(CommonVariables.plugin, onlinePlayer, () -> {
                            onlinePlayer.sendTitle(ChatColor.RED + "Team " + player.getName() + " is eliminated!", "");
                        });
                    }
                }

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                    Scheduler.getScheduler().runPlayerTask(CommonVariables.plugin, onlinePlayer, () -> {
                        onlinePlayer.sendTitle(ChatColor.GREEN + "Team " + aliveTeams.get(0) + " wins!", "", 10, 100, 10);
                    });
                }

                CommonVariables.isGameStarted = false;
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onDeath(PlayerDeathEvent event) {

        if (CommonVariables.isGameStarted) {

            if (CommonVariables.zoneStage < 2) {

                Bukkit.getPluginManager().callEvent(new FirstZoneDeathEvent(event.getPlayer()));
            }
            if (CommonVariables.zoneStage >= 2) {

                Bukkit.getPluginManager().callEvent(new DeathEvent(event.getPlayer()));
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onFirstZoneDeath(FirstZoneDeathEvent event) {}

    @org.bukkit.event.EventHandler
    public void onGameDeath(DeathEvent event) {

        Player player = event.getPlayer();
        Team playerTeam = Team.getTeam(event.getPlayer());
        boolean isTeamDead = true;

        CommonVariables.deadPlayers.add(event.getPlayer().getName());

        if (playerTeam != null) {
            for (String teamMate : playerTeam.getPlayers()) {
                if (!CommonVariables.deadPlayers.contains(teamMate)) {
                    isTeamDead = false;
                    break;
                }
            }
        }

        Scheduler.getScheduler().runPlayerTask(CommonVariables.plugin, player, () -> {
            player.setGameMode(GameMode.SPECTATOR);
        });

        ArrayList<String> aliveTeams = new ArrayList<>();

        if (isTeamDead) {

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                if (onlinePlayer.getGameMode().equals(GameMode.SURVIVAL) && !onlinePlayer.getName().equals(player.getName())) {
                    if (Team.getTeam(onlinePlayer) != null) {
                        if (!aliveTeams.contains(onlinePlayer.getName())) {
                            aliveTeams.add(Team.getTeam(onlinePlayer).getName());
                        }
                    } else {
                        aliveTeams.add(onlinePlayer.getName());
                    }
                }

                if (playerTeam != null) {
                    Scheduler.getScheduler().runPlayerTask(CommonVariables.plugin, onlinePlayer, () -> {
                        onlinePlayer.sendTitle(ChatColor.RED + "Team " + playerTeam.getName() + " is eliminated!", "");
                    });
                } else {
                    Scheduler.getScheduler().runPlayerTask(CommonVariables.plugin, player, () -> {
                        onlinePlayer.sendTitle(ChatColor.RED + "Team " + player.getName() + " is eliminated!", "");
                    });
                }
            }

            if (playerTeam != null) {
                CommonVariables.deadTeams.add(playerTeam.getName());
            }

            if (aliveTeams.size() == 1) {

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                    Scheduler.getScheduler().runPlayerTask(CommonVariables.plugin, onlinePlayer, () -> {
                        onlinePlayer.sendTitle(ChatColor.GREEN + "Team " + aliveTeams.get(0) + " wins!", "", 10, 100, 10);
                    });
                }

                CommonVariables.isGameStarted = false;
                CommonVariables.timer.setColor(BarColor.PINK);
                CommonVariables.timer.setProgress(1);
                CommonVariables.timer.setTitle("Team " + aliveTeams.get(0) + " wins!");
                Bukkit.getConsoleSender().sendMessage("Team " + aliveTeams.get(0) + " wins!");
            }
        }
        if (!isTeamDead) {
            Scheduler.getScheduler().runPlayerTask(CommonVariables.plugin, player, () -> {
                player.kick(Component.text("You are out and your team is not yet!"));
            });
        }
    }
}
