package ru.dvdishka.battleroyale.handlers;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.block.Block;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.Logger;
import ru.dvdishka.battleroyale.logic.Scheduler;
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
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainer;
import ru.dvdishka.battleroyale.logic.event.DeathEvent;
import ru.dvdishka.battleroyale.logic.event.DropClickEvent;
import ru.dvdishka.battleroyale.logic.event.ReviveDeathEvent;
import ru.dvdishka.battleroyale.logic.classes.superpower.SuperPower;
import ru.dvdishka.battleroyale.logic.Team;
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

                int powerNumber = new Random().nextInt(0, SuperPower.values().length);
                SuperPower.values()[powerNumber].setToPlayer(player);

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

                Scheduler.getScheduler().runPlayerTask(Common.plugin, player, (scheduledTask) -> {
                    player.setGameMode(GameMode.SPECTATOR);
                });

                Common.deadPlayers.add(player.getName());

                if (playerTeam != null) {
                    Team.deadTeams.add(playerTeam.getName());
                }

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                    if (playerTeam != null) {
                        Scheduler.getScheduler().runPlayerTask(Common.plugin, onlinePlayer, (scheduledTask) -> {
                            onlinePlayer.sendTitle(ChatColor.RED + "Team " + playerTeam.getName() + " is eliminated!", "");
                        });
                    } else {
                        Scheduler.getScheduler().runPlayerTask(Common.plugin, onlinePlayer, (scheduledTask) -> {
                            onlinePlayer.sendTitle(ChatColor.RED + "Team " + player.getName() + " is eliminated!", "");
                        });
                    }
                }

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                    Scheduler.getScheduler().runPlayerTask(Common.plugin, onlinePlayer, (scheduledTask) -> {
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

            if (Common.isRevivalEnabled) {

                Bukkit.getPluginManager().callEvent(new ReviveDeathEvent(event.getPlayer()));
            }
            if (!Common.isRevivalEnabled) {

                Bukkit.getPluginManager().callEvent(new DeathEvent(event.getPlayer()));
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onReviveDeath(ReviveDeathEvent event) {}

    @org.bukkit.event.EventHandler
    public void onGameDeath(DeathEvent event) {

        Player player = event.getPlayer();
        Team playerTeam = Team.getTeam(event.getPlayer());
        boolean isTeamDead = true;

        Common.deadPlayers.add(event.getPlayer().getName());

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
                    Scheduler.getScheduler().runPlayerTask(Common.plugin, onlinePlayer, (scheduledTask) -> {
                        onlinePlayer.sendTitle(ChatColor.RED + "Team " + playerTeam.getName() + " is eliminated!", "");
                    });
                } else {
                    Scheduler.getScheduler().runPlayerTask(Common.plugin, player, (scheduledTask) -> {
                        onlinePlayer.sendTitle(ChatColor.RED + "Team " + player.getName() + " is eliminated!", "");
                    });
                }
            }

            if (playerTeam != null) {
                Team.deadTeams.add(playerTeam.getName());
            }

            if (aliveTeams.size() == 1) {

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                    Scheduler.getScheduler().runPlayerTask(Common.plugin, onlinePlayer, (scheduledTask) -> {
                        onlinePlayer.sendTitle(ChatColor.GREEN + "Team " + aliveTeams.get(0) + " wins!", "", 10, 100, 10);
                    });
                }

                Common.isGameStarted = false;

                Timer.getInstance().unregister();

                BossBar bossBar = Bukkit.createBossBar("Team " + aliveTeams.get(0) + " wins!", BarColor.PINK, BarStyle.SOLID);

                Bukkit.getConsoleSender().sendMessage("Team " + aliveTeams.get(0) + " wins!");
            }
        }
        if (!isTeamDead) {
            Scheduler.getScheduler().runPlayerTask(Common.plugin, player, (scheduledTask) -> {
                player.kick(Component.text("You are out and your team is not yet!"));
            });
        }
    }

    @org.bukkit.event.EventHandler
    public void onDropContainerExplode(EntityExplodeEvent event) {

        for (Block block : event.blockList()) {

            if (block.hasMetadata("dropContainer")) {

                event.setCancelled(true);
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onDropContainerExplode(BlockExplodeEvent event) {

        for (Block block : event.blockList()) {

            if (block.hasMetadata("dropContainer")) {

                event.setCancelled(true);
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onDropContainerLiquidBreak(BlockFromToEvent event) {

        if (event.getToBlock().hasMetadata("dropContainer")) {
            event.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onDropContainerBreak(BlockBreakEvent event) {

        if (event.getBlock().hasMetadata("dropContainer")) {

            event.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onDropContainerDestroy(BlockDestroyEvent event) {

        if (event.getBlock().hasMetadata("dropContainer")) {

            event.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onDropContainerBurn(BlockBurnEvent event) {

        if (event.getBlock().hasMetadata("dropContainer")) {

            event.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onDropContainerPistonExtend(BlockPistonExtendEvent event) {

        for (Block block : event.getBlocks()) {

            if (block.hasMetadata("dropContainer")) {

                event.setCancelled(true);
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onDropContainerPistonRetract(BlockPistonRetractEvent event) {

        for (Block block : event.getBlocks()) {

            if (block.hasMetadata("dropContainer")) {

                event.setCancelled(true);
            }
        }
    }
}
