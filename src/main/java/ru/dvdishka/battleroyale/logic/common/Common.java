package ru.dvdishka.battleroyale.logic.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.logic.Scheduler;
import ru.dvdishka.battleroyale.logic.Zone;
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainer;
import ru.dvdishka.battleroyale.logic.classes.superpower.SuperPower;
import ru.dvdishka.battleroyale.ui.DropBar;
import ru.dvdishka.battleroyale.ui.Radar;
import ru.dvdishka.battleroyale.ui.Timer;
import ru.dvdishka.battleroyale.ui.WinBar;

import java.util.List;

public class Common {

    public static void resetVariables() {

        GameVariables.isGameStarted = false;
        GameVariables.zoneStage = -1;
        PlayerVariables.deadPlayers.clear();
        PlayerVariables.players.clear();
        ru.dvdishka.battleroyale.logic.Team.deadTeams.clear();
        SuperPower.clearPlayers();
        GameVariables.isPortalLocked = false;
        GameVariables.isRevivalEnabled = true;
        GameVariables.isPVPEnabled = false;
        GameVariables.isWinStage = false;

        PlayerVariables.reviveQueue.clear();
        PlayerVariables.killQueue.clear();

        Zone.unregister();
        Zone.getInstance().setVariables(
                ConfigVariables.defaultWorldBorderDiameter,
                ConfigVariables.defaultWorldBorderDiameter,
                PluginVariables.overWorld.getSpawnLocation().getBlockX(),
                PluginVariables.overWorld.getSpawnLocation().getBlockZ(),
                PluginVariables.overWorld.getSpawnLocation().getBlockX(),
                PluginVariables.overWorld.getSpawnLocation().getBlockZ());

        if (Radar.isInitialized()) {
            Radar.getInstance().movingZoneChar = "=";
            Radar.getInstance().unregister();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            SuperPower.clearPlayerSuperPower(player);
        }

        Scheduler.cancelTasks(PluginVariables.plugin);

        WinBar.getInstance().unregister();
        Timer.getInstance().unregister();
        for (DropBar dropBar : List.copyOf(DropBar.getInstances())) {
            dropBar.unregister();
        }

        for (DropContainer dropContainer : List.copyOf(DropContainer.getContainerList())) {
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

    public static void sendNotification(Component header, Component text, Player player) {

        try {
            Component message = Component.empty();

            message = message
                    .append(Component.text("-".repeat(26))
                            .color(NamedTextColor.DARK_AQUA)
                            .decorate(TextDecoration.BOLD))
                    .append(Component.newline());

            message = message
                    .append(header)
                    .append(Component.newline());

            message = message
                    .append(Component.text("-".repeat(27))
                            .color(NamedTextColor.YELLOW))
                    .append(Component.newline());

            message = message
                    .append(text)
                    .append(Component.newline());

            message = message
                    .append(Component.text("-".repeat(26))
                            .color(NamedTextColor.DARK_AQUA)
                            .decorate(TextDecoration.BOLD));

            player.sendMessage(message);

            Common.notificationSound(player);

        } catch (Exception ignored) {}
    }
}
