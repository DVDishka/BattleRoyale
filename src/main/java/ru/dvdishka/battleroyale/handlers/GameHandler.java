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
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.potion.PotionEffect;
import ru.dvdishka.battleroyale.logic.*;
import ru.dvdishka.battleroyale.logic.classes.superpower.SuperPower;
import ru.dvdishka.battleroyale.logic.event.game.GameDeathEvent;
import ru.dvdishka.battleroyale.logic.event.game.ReviveDeathEvent;
import ru.dvdishka.battleroyale.logic.event.game.TeamEliminateEvent;
import ru.dvdishka.battleroyale.logic.event.game.TeamWinEvent;
import ru.dvdishka.battleroyale.ui.Radar;
import ru.dvdishka.battleroyale.ui.Timer;
import ru.dvdishka.battleroyale.ui.WinBar;

import java.util.ArrayList;
import java.util.Random;

public class GameHandler implements Listener {

    @EventHandler
    public void onTeamWin(TeamWinEvent event) {

        Zone.getInstance().stopMoving();
        Timer.getInstance().unregister();

        Common.isWinStage = true;

        WinBar.getInstance().register(event.getTeamName(), NamedTextColor.nearestTo(event.getTeamColor()));

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            if (Common.players.contains(onlinePlayer.getName())) {

                Scheduler.getScheduler().runPlayerTask(Common.plugin, onlinePlayer, (scheduledTask) -> {

                    if (Common.players.contains(onlinePlayer.getName())) {
                        onlinePlayer.playSound(onlinePlayer, Sound.ITEM_GOAT_HORN_SOUND_1, 1000, 0);
                    }

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
        }

        Logger.getLogger().log("Team " + event.getTeamName() + " wins!");
    }

    @EventHandler
    public void onTeamEliminate(TeamEliminateEvent event) {

        Team.deadTeams.add(event.getTeamName());

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            if (Common.players.contains(onlinePlayer.getName())) {

                Scheduler.getScheduler().runPlayerTask(Common.plugin, onlinePlayer, (scheduledTask) -> {

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
    }

    @org.bukkit.event.EventHandler
    public void onGameDeath(GameDeathEvent event) {

        Player player = event.getPlayer();
        Team playerTeam = Team.getTeam(event.getPlayer());
        boolean isTeamDead = true;

        String playerTeamName = player.getName();
        TextColor playerTeamColor = NamedTextColor.WHITE;
        if (playerTeam != null) {
            playerTeamName = playerTeam.getName();
            playerTeamColor = playerTeam.getColor();
        }

        Common.deadPlayers.add(event.getPlayer().getName());

        for (Player onlinePayer : Bukkit.getOnlinePlayers()) {
            if (Common.players.contains(onlinePayer.getName())) {

                Component header = Component.empty();
                Component text = Component.empty();

                header = header
                        .append(Component.text(event.getPlayer().getName())
                                .decorate(TextDecoration.BOLD));

                text = text
                        .append(Component.text("Player is eliminated!")
                                .color(NamedTextColor.RED));

                Common.sendNotification(header, text, onlinePayer);
            }
        }

        if (playerTeam != null) {
            for (String teamMate : playerTeam.getMembers()) {
                if (!Common.deadPlayers.contains(teamMate)) {
                    isTeamDead = false;
                    break;
                }
            }
        }

        Scheduler.getScheduler().runPlayerTask(Common.plugin, player, (scheduledTask) -> {
            player.setGameMode(GameMode.SPECTATOR);
        });

        ArrayList<String> aliveTeams = getAliveTeams(player);

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
            Scheduler.getScheduler().runPlayerTask(Common.plugin, player, (scheduledTask) -> {
                player.kick(Component.text("You are out and your team is not yet!"));
            });
        }
    }

    @EventHandler
    public void onReviveDeath(ReviveDeathEvent event) {}

    public static ArrayList<String> getAliveTeams(Player notCheckedPlayer) {

        ArrayList<String> aliveTeams = new ArrayList<>();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            if (Common.players.contains(onlinePlayer.getName()) && !onlinePlayer.getName().equals(notCheckedPlayer.getName())) {
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
}
