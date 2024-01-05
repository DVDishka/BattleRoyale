package ru.dvdishka.battleroyale.handlers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import ru.dvdishka.battleroyale.logic.*;
import ru.dvdishka.battleroyale.logic.classes.superpower.SuperPower;
import ru.dvdishka.battleroyale.logic.common.Common;
import ru.dvdishka.battleroyale.logic.common.GameVariables;
import ru.dvdishka.battleroyale.logic.common.PlayerVariables;
import ru.dvdishka.battleroyale.logic.common.PluginVariables;
import ru.dvdishka.battleroyale.logic.event.game.GameDeathEvent;
import ru.dvdishka.battleroyale.logic.event.game.TeamEliminateEvent;
import ru.dvdishka.battleroyale.logic.event.game.TeamWinEvent;
import ru.dvdishka.battleroyale.ui.Radar;
import ru.dvdishka.battleroyale.ui.Timer;
import ru.dvdishka.battleroyale.ui.WinBar;

import java.util.ArrayList;

public class GameHandler implements Listener {

    @EventHandler
    public void onTeamWin(TeamWinEvent event) {

        Zone.getInstance().stopMoving();
        Timer.getInstance().unregister();

        GameVariables.isWinStage = true;

        WinBar.getInstance().register(event.getTeamName());

        for (Player onlinePlayer : PlayerVariables.getOnlinePlayers()) {

            Scheduler.getScheduler().runPlayerTask(PluginVariables.plugin, onlinePlayer, (scheduledTask) -> {

                onlinePlayer.playSound(onlinePlayer, Sound.ITEM_GOAT_HORN_SOUND_1, 1000, 0);

                Component header = Component.empty();
                Component text = Component.empty();

                header = header
                        .append(Component.text(event.getTeamName())
                                .color(event.getTeamColor())
                                .decorate(TextDecoration.BOLD));

                text = text
                        .append(Component.text("Team wins"));

                Common.sendNotification(header, text, onlinePlayer);
            });
        }

        Logger.getLogger().log("Team " + event.getTeamName() + " wins!");
    }

    @EventHandler
    public void onTeamEliminate(TeamEliminateEvent event) {

        Team.deadTeams.add(event.getTeamName());

        for (Player onlinePlayer : PlayerVariables.getOnlinePlayers()) {

            Scheduler.getScheduler().runPlayerTask(PluginVariables.plugin, onlinePlayer, (scheduledTask) -> {

                Component header = Component.empty();
                Component text = Component.empty();

                header = header
                        .append(Component.text(event.getTeamName())
                                .color(event.getTeamColor())
                                .decorate(TextDecoration.BOLD));

                text = text
                        .append(Component.text("Team is eliminated")
                                .color(NamedTextColor.RED));

                Common.sendNotification(header, text, onlinePlayer);
            });
        }
    }

    @org.bukkit.event.EventHandler
    public void onGameDeath(GameDeathEvent event) {

        String playerName = event.getPlayerName();
        Team playerTeam = Team.getTeam(playerName);
        boolean isTeamDead = true;

        String playerTeamName = playerName;
        TextColor playerTeamColor = NamedTextColor.WHITE;
        if (playerTeam != null) {
            playerTeamName = playerTeam.getName();
            playerTeamColor = playerTeam.getColor();
        }

        PlayerVariables.addDead(playerName);

        for (Player onlinePayer : PlayerVariables.getOnlinePlayers()) {

            Component header = Component.empty();
            Component text = Component.empty();

            header = header
                    .append(Component.text(playerName)
                            .decorate(TextDecoration.BOLD));

            text = text
                    .append(Component.text("Player is eliminated!")
                            .color(NamedTextColor.RED));

            Common.sendNotification(header, text, onlinePayer);
        }

        if (playerTeam != null) {
            for (String teamMate : playerTeam.getMembers()) {
                if (!PlayerVariables.isDead(teamMate)) {
                    isTeamDead = false;
                    break;
                }
            }
        }

        if (Bukkit.getPlayer(playerName) != null) {

            Player player = Bukkit.getPlayer(playerName);

            Scheduler.getScheduler().runPlayerTask(PluginVariables.plugin, player, (scheduledTask) -> {
                player.setGameMode(GameMode.SPECTATOR);
            });
        }

        ArrayList<String> aliveTeams = getAliveTeams(playerName);

        if (isTeamDead) {

            Bukkit.getPluginManager().callEvent(new TeamEliminateEvent(playerTeamName, playerTeamColor));

            if (aliveTeams.size() == 1) {

                String winTeamName = aliveTeams.get(0);
                TextColor winTeamColor = NamedTextColor.WHITE;
                if (Team.get(winTeamName) != null) {
                    winTeamColor = Team.get(winTeamName).getColor();
                }
                Bukkit.getPluginManager().callEvent(new TeamWinEvent(aliveTeams.get(0), winTeamColor));
            }
        } else {

            if (Bukkit.getPlayer(playerName) != null) {

                Player player = Bukkit.getPlayer(playerName);

                Scheduler.getScheduler().runPlayerTask(PluginVariables.plugin, player, (scheduledTask) -> {
                    player.kick(Component.text("You are out and your team is not yet!"));
                });
            }
        }
    }

    @EventHandler
    public void onReviveRespawn(PlayerRespawnEvent event) {
        if (GameVariables.isRevivalEnabled) {
            StartElytraHandler.giveStartElytra(event.getPlayer());
        }
    }

    public static ArrayList<String> getAliveTeams(String notCheckedPlayer) {

        ArrayList<String> aliveTeams = new ArrayList<>();

        for (Player onlinePlayer : PlayerVariables.getOnlinePlayers()) {

            if (!onlinePlayer.getName().equals(notCheckedPlayer) &&
                    !PlayerVariables.isDead(onlinePlayer.getName())) {

                if (Team.getTeam(onlinePlayer) != null) {
                    if (!aliveTeams.contains(onlinePlayer.getName())) {
                        aliveTeams.add(Team.getTeam(onlinePlayer).getName());
                    }
                } else {
                    aliveTeams.add(onlinePlayer.getName());
                }
            }
        }
        return aliveTeams;
    }

    @org.bukkit.event.EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (GameVariables.isGameStarted) {

            if (PlayerVariables.isDead(event.getPlayer().getName()) &&
                    Team.getTeam(event.getPlayer()) != null &&
                    !Team.deadTeams.contains(Team.getTeam(event.getPlayer()).getName())) {

                Scheduler.getScheduler().runPlayerTask(PluginVariables.plugin, event.getPlayer(), (scheduledTask) -> {
                    event.getPlayer().kick(Component.text("You are out and your team is not yet!"));
                });
            }

            else if (PlayerVariables.isDead(event.getPlayer().getName())) {
                Scheduler.getScheduler().runPlayerTask(PluginVariables.plugin, event.getPlayer(), (scheduledTask) -> {
                    event.getPlayer().setGameMode(GameMode.SPECTATOR);
                });
            }
        }

        if (GameVariables.isGameStarted) {
            Timer.getInstance().addViewer(event.getPlayer());
            Radar.getInstance().addViewer(event.getPlayer());
        }

        Player player = event.getPlayer();

        Scheduler.getScheduler().runPlayerTask(PluginVariables.plugin, player, (scheduledTask) -> {

            if (!PlayerVariables.isBattleRoyalePlayer(player.getName()) && GameVariables.isGameStarted) {

                player.setGameMode(GameMode.SURVIVAL);

                ArrayList<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());
                for (PotionEffect effect : effects) {
                    player.removePotionEffect(effect.getType());
                }

                if (!SuperPower.isEmpty()) {
                    SuperPower.getRandom().setToPlayer(player);
                }

                PlayerVariables.addBattleRoyalePlayer(player.getName());
            }
        });

        if (PlayerVariables.isKillQueue(player.getName())) {

            Scheduler.getScheduler().runPlayerTask(PluginVariables.plugin, player, (scheduledTask) -> {
                player.setGameMode(GameMode.SPECTATOR);
            });

            PlayerVariables.removeKillQueue(player.getName());
        }

        if (PlayerVariables.isReviveQueue(player)) {

            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(PluginVariables.overWorld.getSpawnLocation());
            StartElytraHandler.giveStartElytra(player);

            PlayerVariables.removeReviveQueue(player);
        }
    }
}
