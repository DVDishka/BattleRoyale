package ru.dvdishka.battleroyale.logic;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Team;
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainer;
import ru.dvdishka.battleroyale.logic.classes.superpower.SuperPower;
import ru.dvdishka.battleroyale.ui.DropBar;
import ru.dvdishka.battleroyale.ui.Radar;
import ru.dvdishka.battleroyale.ui.Timer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

public class Common {

    public static Plugin plugin;
    public static final Logger logger = Bukkit.getLogger();

    public static boolean isFolia = false;

    public static volatile boolean isGameStarted = false;
    public static volatile boolean isBreak = false;
    public static volatile boolean isRevivalEnabled = true;
    public static volatile boolean isPVPEnabled = false;

    public static int zoneStage = -1;
    public static boolean isStartBox = false;
    public static volatile boolean isPortalLocked = false;

    public static HashSet<String> deadPlayers = new HashSet<>();
    public static HashSet<String> players = new HashSet<>();
    public static HashMap<String, SuperPower> playersPower = new HashMap<>();

    public static void resetVariables() {

        Common.isGameStarted = false;
        Common.zoneStage = -1;
        Common.deadPlayers.clear();
        Common.players.clear();
        ru.dvdishka.battleroyale.logic.Team.deadTeams.clear();
        Common.playersPower.clear();
        Common.isPortalLocked = false;
        Common.isRevivalEnabled = true;
        Common.isPVPEnabled = false;

        Zone.getInstance().setZoneMoving(false);
        Zone.getInstance().setVariables(
                ConfigVariables.defaultWorldBorderDiameter,
                ConfigVariables.defaultWorldBorderDiameter,
                0,
                0,
                0,
                0);

        if (Radar.isInitialized()) {
            Radar.getInstance().movingZoneChar = "=";
            Radar.getInstance().unregister();
        }

        for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            team.unregister();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (PotionEffect potionEffectType : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffectType.getType());
            }
        }

        Scheduler.cancelTasks(Common.plugin);

        Timer.getInstance().unregister();
        for (DropBar dropBar : DropBar.getInstances()) {
            dropBar.unregister();
        }

        for (DropContainer dropContainer : DropContainer.getContainerList()) {
            dropContainer.delete();
        }

        for (World world : Bukkit.getWorlds()) {
            world.setPVP(true);
            world.getWorldBorder().setCenter(0, 0);
            world.getWorldBorder().setSize(ConfigVariables.defaultWorldBorderDiameter);
        }
    }

    public static void returnFailure(String message, CommandSender sender) {
        try {
            sender.sendMessage(Component.text(message).color(NamedTextColor.RED));
        } catch (Exception ignored) {}
    }

    public static void returnFailure(String message, CommandSender sender, TextColor color) {
        try {
            sender.sendMessage(Component.text(message).color(color));
        } catch (Exception ignored) {}
    }

    public static void returnSuccess(String message, CommandSender sender) {
        try {
            sender.sendMessage(Component.text(message).color(NamedTextColor.GREEN));
        } catch (Exception ignored) {}
    }

    public static void returnSuccess(String message, CommandSender sender, TextColor color) {
        try {
            sender.sendMessage(color + message);
        } catch (Exception ignored) {}
    }

    public static void returnWarning(String message, CommandSender sender) {
        try {
            sender.sendMessage(Component.text(message).color(TextColor.color(211, 145, 0)));
        } catch (Exception ignored) {}
    }


    public static void returnWarning(String message, CommandSender sender, TextColor color) {
        try {
            sender.sendMessage(color + message);
        } catch (Exception ignored) {}
    }

    public static void sendMessage(String message, @NotNull CommandSender sender) {
        try {
            sender.sendMessage(message);
        } catch (Exception ignored) {}
    }

    public static void notificationSound(Player player) {
        try {
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 75, 100);
        } catch (Exception ignored) {}
    }

    public static void buttonSound(Player player) {
        try {
            player.playSound(player, Sound.UI_BUTTON_CLICK, 75, 10);
        } catch (Exception ignored) {}
    }
}
