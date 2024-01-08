package ru.dvdishka.battleroyale.handlers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import ru.dvdishka.battleroyale.logic.*;
import ru.dvdishka.battleroyale.logic.classes.ZonePhase;
import ru.dvdishka.battleroyale.logic.classes.drop.DropType;
import ru.dvdishka.battleroyale.logic.common.Common;
import ru.dvdishka.battleroyale.logic.common.ConfigVariables;
import ru.dvdishka.battleroyale.logic.common.GameVariables;
import ru.dvdishka.battleroyale.logic.common.PluginVariables;
import ru.dvdishka.battleroyale.logic.event.drop.DropCreateEvent;
import ru.dvdishka.battleroyale.logic.event.NextGameStageEvent;
import ru.dvdishka.battleroyale.ui.Radar;
import ru.dvdishka.battleroyale.ui.Timer;

import java.util.Random;

public class ZoneStageHandler implements Listener  {

    @org.bukkit.event.EventHandler
    public void onNextGameStageEvent(NextGameStageEvent event) {

        if (!GameVariables.isGameStarted && !GameVariables.isWinStage) {
            return;
        }

        GameVariables.zoneStage++;

        // REVIVE DISABLE LOGIC
        if (GameVariables.zoneStage == ConfigVariables.lastReviveZone) {

            GameVariables.isRevivalEnabled = false;

            for (Player player : Bukkit.getOnlinePlayers()) {

                Component header = Component.empty();
                Component text = Component.empty();

                header = header
                        .append(Component.text("Revival")
                                .decorate(TextDecoration.BOLD));

                text = text
                        .append(Component.text("Revival is now disabled")
                                .color(NamedTextColor.RED));

                Common.sendNotification(header, text, player);
            }
        }

        // PVP ENABLE LOGIC
        if (GameVariables.zoneStage == ConfigVariables.pvpEnableZone) {

            GameVariables.isPVPEnabled = true;

            for (World world : Bukkit.getWorlds()) {
                world.setPVP(true);
            }

            for (Player player : Bukkit.getOnlinePlayers()) {

                Component header = Component.empty();
                Component text = Component.empty();

                header = header
                        .append(Component.text("PVP")
                                .decorate(TextDecoration.BOLD));

                text = text
                        .append(Component.text("PVP is now enabled")
                                .color(NamedTextColor.RED));

                Common.sendNotification(header, text, player);
            }
        }

        // ZONE MOVING STAGE LOGIC
        if (GameVariables.zoneStage >= ConfigVariables.zones.size()) {
            zoneMovingStage();
        }

        // FIRST ZONE STAGE LOGIC
        else if (GameVariables.zoneStage == 0) {
            firstZoneStage();
        }

        // MAIN STAGE LOGIC
        else {
            mainNextStageLogic();
        }
    }

    private void firstZoneStage() {

        int timeOut = ConfigVariables.timeOuts.get(0);
        final int oldZoneCenterX = PluginVariables.overWorld.getWorldBorder().getCenter().getBlockX();
        final int oldZoneCenterZ = PluginVariables.overWorld.getWorldBorder().getCenter().getBlockZ();
        int previousZoneDiameter = ConfigVariables.defaultWorldBorderDiameter;

        final int nextZoneCenterX = Zone.getInstance().generateRandomZoneCenterX(
                ConfigVariables.defaultWorldBorderDiameter / 2,
                ConfigVariables.zones.get(GameVariables.zoneStage) / 2,
                oldZoneCenterX);
        final int nextZoneCenterZ = Zone.getInstance().generateRandomZoneCenterZ(
                ConfigVariables.defaultWorldBorderDiameter / 2,
                ConfigVariables.zones.get(GameVariables.zoneStage) / 2,
                oldZoneCenterZ);

        Zone.getInstance().setVariables(ConfigVariables.defaultWorldBorderDiameter,
                ConfigVariables.zones.get(GameVariables.zoneStage),
                oldZoneCenterX,
                oldZoneCenterZ,
                nextZoneCenterX,
                nextZoneCenterZ);

        // BREAK BOSS BAR TIMER TASK START
        Scheduler.getScheduler().runSync(PluginVariables.plugin, (scheduledTask) -> {
            GameVariables.isBreak = true;
            Timer.getInstance().startTimer(timeOut, ZonePhase.BREAK, false);
        });

        // ACTIVE BORDERS TASK START
        Scheduler.getScheduler().runSyncDelayed(PluginVariables.plugin, (scheduledTask) -> {

            GameVariables.isBreak = false;

            // SPAWN DROP CONTAINERS
            for (World world : ConfigVariables.dropSpawnWorlds) {
                Bukkit.getPluginManager().callEvent(new DropCreateEvent(DropType.getRandomType(), world));
            }

            // CHANGE WORLD BORDER
            Zone.getInstance().changeBorders(
                    ConfigVariables.defaultWorldBorderDiameter,
                    ConfigVariables.zones.get(GameVariables.zoneStage),
                    ConfigVariables.times.get(GameVariables.zoneStage),
                    oldZoneCenterX,
                    oldZoneCenterZ,
                    nextZoneCenterX,
                    nextZoneCenterZ);

            // ACTIVE BOSS BAR TIMER TASK START
            Timer.getInstance().startTimer(ConfigVariables.times.get(GameVariables.zoneStage), ZonePhase.ACTIVE, true);
            }, timeOut * 20L);
    }

    public void mainNextStageLogic() {

        int timeOut = ConfigVariables.timeOuts.get(GameVariables.zoneStage);
        int previousZoneDiameter = ConfigVariables.zones.get(GameVariables.zoneStage - 1);

        final int oldZoneCenterX = Zone.getInstance().getNewZoneCenterX();
        final int oldZoneCenterZ = Zone.getInstance().getNewZoneCenterZ();

        final int nextZoneCenterX = Zone.getInstance().generateRandomZoneCenterX(
                previousZoneDiameter / 2,
                ConfigVariables.zones.get(GameVariables.zoneStage) / 2,
                oldZoneCenterX);
        final int nextZoneCenterZ = Zone.getInstance().generateRandomZoneCenterZ(
                previousZoneDiameter / 2,
                ConfigVariables.zones.get(GameVariables.zoneStage) / 2,
                oldZoneCenterZ);

        Zone.getInstance().setVariables(ConfigVariables.zones.get(GameVariables.zoneStage - 1),
                ConfigVariables.zones.get(GameVariables.zoneStage),
                oldZoneCenterX,
                oldZoneCenterZ,
                nextZoneCenterX,
                nextZoneCenterZ);

        // BREAK BOSS BAR TIMER TASK START
        Scheduler.getScheduler().runSync(PluginVariables.plugin, (scheduledTask) -> {

            GameVariables.isBreak = true;

            Timer.getInstance().startTimer(timeOut, ZonePhase.BREAK, false);
        });

        // ACTIVE BORDERS TASK START
        Scheduler.getScheduler().runSyncDelayed(PluginVariables.plugin, (scheduledTask) -> {

            GameVariables.isBreak = false;

            // SPAWN DROP CONTAINERS
            for (World world : ConfigVariables.dropSpawnWorlds) {
                Bukkit.getPluginManager().callEvent(new DropCreateEvent(DropType.getRandomType(), world));
            }

            // CHANGE WORLD BORDER
            Zone.getInstance().changeBorders(
                    ConfigVariables.zones.get(GameVariables.zoneStage - 1),
                    ConfigVariables.zones.get(GameVariables.zoneStage),
                    ConfigVariables.times.get(GameVariables.zoneStage),
                    oldZoneCenterX,
                    oldZoneCenterZ,
                    nextZoneCenterX,
                    nextZoneCenterZ);

            // ACTIVE BOSS BAR TIMER TASK START
            Timer.getInstance().startTimer(ConfigVariables.times.get(GameVariables.zoneStage), ZonePhase.ACTIVE, true);
            }, timeOut * 20L);
    }

    private void zoneMovingStage() {

        // TELEPORT PLAYERS IF NOT IN OVERWORLD AND LOCK PORTALS
        for (Player player : Bukkit.getOnlinePlayers()) {
            teleportPlayerToOverworld(player);
        }
        GameVariables.isPortalLocked = true;

        int side = new Random().nextInt(0, 4);
        int moveLength = new Random().nextInt(ConfigVariables.minFinalZoneMove, ConfigVariables.maxFinalZoneMove + 1) / 10 * 10;
        String sideName;
        int x, z;

        switch (side) {

            // East
            case 0 -> {
                Radar.getInstance().movingZoneChar = ">";
                x = 1;
                z = 0;
                sideName = "East";
            }

            // WEST
            case 1 -> {
                Radar.getInstance().movingZoneChar = "<";
                x = -1;
                z = 0;
                sideName = "West";
            }

            // SOUTH
            case 2 -> {
                Radar.getInstance().movingZoneChar = "V";
                z = 1;
                x = 0;
                sideName = "South";
            }

            // NORTH
            case 3 -> {
                Radar.getInstance().movingZoneChar = "A";
                z = -1;
                x = 0;
                sideName = "North";
            }

            default -> {
                Radar.getInstance().movingZoneChar = "=";
                x = 0;
                z = 0;
                sideName = "";
            }
        }

        Zone.getInstance().setVariables(
                ConfigVariables.zones.get(ConfigVariables.zones.size() - 1),
                ConfigVariables.zones.get(ConfigVariables.zones.size() - 1),
                Zone.getInstance().getNewZoneCenterX(),
                Zone.getInstance().getNewZoneCenterZ(),
                Zone.getInstance().getNewZoneCenterX() + x * moveLength,
                Zone.getInstance().getNewZoneCenterZ() + z * moveLength);

        // BREAK BOSS BAR TIMER TASK START
        Scheduler.getScheduler().runSync(PluginVariables.plugin, (scheduledTask) -> {
            GameVariables.isBreak = true;
            Timer.getInstance().startTimer(ConfigVariables.zoneMoveTimeOut, ZonePhase.BREAK, false);
        });

        // MOVE BORDERS MOVING TASK START
        Scheduler.getScheduler().runSyncDelayed(PluginVariables.plugin, (scheduledTask) -> {
            GameVariables.isBreak = false;
            Timer.getInstance().startTimer(ConfigVariables.finalZoneMoveDuration, ZonePhase.MOVE, true);
            Zone.getInstance().moveZone(x, z, ConfigVariables.finalZoneMoveDuration, Math.abs(moveLength));
        }, ConfigVariables.zoneMoveTimeOut * 20L);
    }

    public static void teleportPlayerToOverworld(Player player) {

        if (!player.getWorld().getKey().equals(PluginVariables.overWorld.getKey())) {

            for (int i = 5; i > 0; i--) {

                final TextColor numberColor;

                if (i > 1 && i < 4) {
                    numberColor = NamedTextColor.YELLOW;
                } else if (i == 1) {
                    numberColor = NamedTextColor.RED;
                } else {
                    numberColor = NamedTextColor.GREEN;
                }

                final int number = 5 - i;

                Scheduler.getScheduler().runSyncDelayed(PluginVariables.plugin, (scheduledTask) -> {
                            player.sendTitlePart(TitlePart.TITLE, Component.text(number).color(numberColor).decorate(TextDecoration.BOLD));
                            player.sendTitlePart(TitlePart.SUBTITLE,
                                    Component.empty()
                                            .append(Component.text("You will be teleported to"))
                                            .append(Component.space())
                                            .append(Component.text("OVERWORLD")
                                                    .color(NamedTextColor.DARK_GREEN)));
                            }, i * 20L);
            }

            Scheduler.getScheduler().runSyncDelayed(PluginVariables.plugin, (scheduledTask) -> {

                StartElytraHandler.giveStartElytra(player);

                player.teleport(new Location(PluginVariables.overWorld,
                        Zone.getInstance().getCurrentZoneCenterX(),
                        PluginVariables.overWorld.getMaxHeight(),
                        Zone.getInstance().getCurrentZoneCenterZ())
                );}, 100);
        }
    }
}
