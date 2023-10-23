package ru.dvdishka.battleroyale.handlers;

import ru.dvdishka.battleroyale.classes.*;
import ru.dvdishka.battleroyale.common.Common;
import ru.dvdishka.battleroyale.common.Scheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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

        Radar.getInstance().addViewer(event.getPlayer());

        if (Common.isGameStarted) {
            if (Common.deadPlayers.contains(event.getPlayer().getName()) &&
                    Team.getTeam(event.getPlayer()) != null &&
                    !Common.deadTeams.contains(Team.getTeam(event.getPlayer()).getName())) {

                Scheduler.getScheduler().runPlayerTask(Common.plugin, event.getPlayer(), () -> {
                    event.getPlayer().kick(Component.text("You are out and your team is not yet!"));
                });
            }
        }

        Common.timer.addPlayer(event.getPlayer());

        Player player = event.getPlayer();

        Scheduler.getScheduler().runPlayerTask(Common.plugin, player, () -> {

            if (!Common.players.contains(player.getName()) && Common.isGameStarted) {

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
    public void onPortal(PlayerPortalEvent event) {

        if (Common.isPortalsLocked) {

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

                Scheduler.getScheduler().runPlayerTask(Common.plugin, player, () -> {
                    player.setGameMode(GameMode.SPECTATOR);
                });

                Common.deadPlayers.add(player.getName());

                if (playerTeam != null) {
                    Common.deadTeams.add(playerTeam.getName());
                }

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                    if (playerTeam != null) {
                        Scheduler.getScheduler().runPlayerTask(Common.plugin, onlinePlayer, () -> {
                            onlinePlayer.sendTitle(ChatColor.RED + "Team " + playerTeam.getName() + " is eliminated!", "");
                        });
                    } else {
                        Scheduler.getScheduler().runPlayerTask(Common.plugin, onlinePlayer, () -> {
                            onlinePlayer.sendTitle(ChatColor.RED + "Team " + player.getName() + " is eliminated!", "");
                        });
                    }
                }

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                    Scheduler.getScheduler().runPlayerTask(Common.plugin, onlinePlayer, () -> {
                        onlinePlayer.sendTitle(ChatColor.GREEN + "Team " + aliveTeams.get(0) + " wins!", "", 10, 100, 10);
                    });
                }

                Common.isGameStarted = false;
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onDeath(PlayerDeathEvent event) {

        if (Common.isGameStarted) {

            if (Common.zoneStage < 2) {

                Bukkit.getPluginManager().callEvent(new FirstZoneDeathEvent(event.getPlayer()));
            }
            if (Common.zoneStage >= 2) {

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

        Common.deadPlayers.add(event.getPlayer().getName());

        if (playerTeam != null) {
            for (String teamMate : playerTeam.getPlayers()) {
                if (!Common.deadPlayers.contains(teamMate)) {
                    isTeamDead = false;
                    break;
                }
            }
        }

        Scheduler.getScheduler().runPlayerTask(Common.plugin, player, () -> {
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
                    Scheduler.getScheduler().runPlayerTask(Common.plugin, onlinePlayer, () -> {
                        onlinePlayer.sendTitle(ChatColor.RED + "Team " + playerTeam.getName() + " is eliminated!", "");
                    });
                } else {
                    Scheduler.getScheduler().runPlayerTask(Common.plugin, player, () -> {
                        onlinePlayer.sendTitle(ChatColor.RED + "Team " + player.getName() + " is eliminated!", "");
                    });
                }
            }

            if (playerTeam != null) {
                Common.deadTeams.add(playerTeam.getName());
            }

            if (aliveTeams.size() == 1) {

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                    Scheduler.getScheduler().runPlayerTask(Common.plugin, onlinePlayer, () -> {
                        onlinePlayer.sendTitle(ChatColor.GREEN + "Team " + aliveTeams.get(0) + " wins!", "", 10, 100, 10);
                    });
                }

                Common.isGameStarted = false;
                Common.timer.setColor(BarColor.PINK);
                Common.timer.setProgress(1);
                Common.timer.setTitle("Team " + aliveTeams.get(0) + " wins!");
                Bukkit.getConsoleSender().sendMessage("Team " + aliveTeams.get(0) + " wins!");
            }
        }
        if (!isTeamDead) {
            Scheduler.getScheduler().runPlayerTask(Common.plugin, player, () -> {
                player.kick(Component.text("You are out and your team is not yet!"));
            });
        }
    }
}
