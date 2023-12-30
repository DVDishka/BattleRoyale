package ru.dvdishka.battleroyale.handlers;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.potion.PotionEffect;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.Scheduler;
import ru.dvdishka.battleroyale.logic.Team;
import ru.dvdishka.battleroyale.logic.classes.superpower.SuperPower;
import ru.dvdishka.battleroyale.logic.event.game.GameDeathEvent;
import ru.dvdishka.battleroyale.logic.event.game.ReviveDeathEvent;
import ru.dvdishka.battleroyale.ui.Radar;
import ru.dvdishka.battleroyale.ui.Timer;

import java.util.ArrayList;
import java.util.Random;

public class EventHandler implements Listener {

    @org.bukkit.event.EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (Common.isGameStarted) {

            if (Common.deadPlayers.contains(event.getPlayer().getName()) &&
                    Team.getTeam(event.getPlayer()) != null &&
                    !Team.deadTeams.contains(Team.getTeam(event.getPlayer()).getName())) {

                Scheduler.getScheduler().runPlayerTask(Common.plugin, event.getPlayer(), (scheduledTask) -> {
                    event.getPlayer().kick(Component.text("You are out and your team is not yet!"));
                });
            }

            else if (Common.deadPlayers.contains(event.getPlayer().getName())) {
                Scheduler.getScheduler().runPlayerTask(Common.plugin, event.getPlayer(), (scheduledTask) -> {
                    event.getPlayer().setGameMode(GameMode.SPECTATOR);
                });
            }
        }

        if (Common.isGameStarted) {
            Timer.getInstance().addViewer(event.getPlayer());
            Radar.getInstance().addViewer(event.getPlayer());
        }

        Player player = event.getPlayer();

        Scheduler.getScheduler().runPlayerTask(Common.plugin, player, (scheduledTask) -> {

            if (!Common.players.contains(player.getName()) && Common.isGameStarted) {

                player.setGameMode(GameMode.SURVIVAL);

                ArrayList<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());
                for (PotionEffect effect : effects) {
                    player.removePotionEffect(effect.getType());
                }

                if (!SuperPower.isEmpty()) {
                    SuperPower.getRandom().setToPlayer(player);
                }

                Common.players.add(player.getName());
            }
        });
    }

    @org.bukkit.event.EventHandler
    public void onPortal(PlayerPortalEvent event) {

        if (Common.isPortalLocked) {

            event.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onDeath(PlayerDeathEvent event) {

        if (Common.isGameStarted) {

            if (Common.isRevivalEnabled) {

                Bukkit.getPluginManager().callEvent(new ReviveDeathEvent(event.getPlayer()));
            }
            if (!Common.isRevivalEnabled) {

                Bukkit.getPluginManager().callEvent(new GameDeathEvent(event.getPlayer().getName()));
            }
        }
    }
}
