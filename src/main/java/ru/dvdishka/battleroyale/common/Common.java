package ru.dvdishka.battleroyale.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.dvdishka.battleroyale.classes.SuperPower;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

public class Common {

    public static Plugin plugin;
    public static final Logger logger = Bukkit.getLogger();

    public static boolean isFolia = false;

    public static boolean isGameStarted = false;
    public static int zoneStage = 0;
    public static BossBar timer = Bukkit.createBossBar("", BarColor.RED, BarStyle.SEGMENTED_10);
    public static boolean isStartBox = false;
    public static volatile boolean isPortalLocked = false;

    public static HashSet<String> deadPlayers = new HashSet<>();
    public static HashSet<String> players = new HashSet<>();
    public static HashMap<String, SuperPower> playersPower = new HashMap<>();

    public static void resetVariables() {

        Common.isGameStarted = false;
        Common.zoneStage = 0;
        Common.deadPlayers.clear();
        Common.players.clear();
        ru.dvdishka.battleroyale.classes.Team.deadTeams.clear();
        Common.playersPower.clear();
        Common.isPortalLocked = false;

        for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            team.unregister();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (PotionEffect potionEffectType : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffectType.getType());
            }
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
}
