package ru.dvdishka.battleroyale.common;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.catcoder.sidebar.ProtocolSidebar;
import me.catcoder.sidebar.Sidebar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.dvdishka.battleroyale.classes.Team;
import ru.dvdishka.battleroyale.commands.*;
import ru.dvdishka.battleroyale.commands.common.Permission;
import ru.dvdishka.battleroyale.commands.startbox.Create;
import ru.dvdishka.battleroyale.commands.startbox.Remove;
import ru.dvdishka.battleroyale.commands.team.Invite;
import ru.dvdishka.battleroyale.commands.team.Leave;
import ru.dvdishka.battleroyale.commands.team.List;
import ru.dvdishka.battleroyale.handlers.EventHandler;
import ru.dvdishka.battleroyale.handlers.Radar;
import ru.dvdishka.battleroyale.handlers.ZoneStageHandler;

import java.util.ArrayList;

public class Initialization {

    public static void checkDependencies() {

        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
            Common.isFolia = true;
            Logger.getLogger().devLog("Paper/Folia has been detected!");
        } catch (Exception e) {
            Common.isFolia = false;
            Logger.getLogger().devLog("Paper/Folia has not been detected!");
        }
    }

    public static void initConfig() {

        FileConfiguration config = Common.plugin.getConfig();

        ConfigVariables.defaultWorldBorderDiameter = config.getInt("defaultWorldBorderRadius", 5000) * 2;

        ArrayList<Integer> zones = new ArrayList<>();
        for (int zoneRadius : config.getIntegerList("zones")) {
            zones.add(zoneRadius * 2);
        }
        ConfigVariables.zones = zones;

        ConfigVariables.times = config.getIntegerList("times");
        ConfigVariables.timeOut = config.getInt("timeOut", 600);
        ConfigVariables.finalZoneDiameter = config.getInt("finalZoneRadius", 50) * 2;
        ConfigVariables.finalZoneTimeOut = config.getInt("finalZoneTimeOut", 200);
        ConfigVariables.finalZoneDuration = config.getInt("finalZoneDuration", 120);
        ConfigVariables.finalZoneMoveDuration = config.getInt("finalZoneMoveDuration", 120);
        if (config.getInt("minFinalZoneMove", 50) < config.getInt("maxFinalZoneMove", 100)) {
            ConfigVariables.minFinalZoneMove = config.getInt("minFinalZoneMove", 50);
            ConfigVariables.maxFinalZoneMove = config.getInt("maxFinalZoneMove", 100);
        } else {
            Logger.getLogger().warn("Config exception! maxFinalZoneMove can not be less than maxFinalZoneMove");
        }
        ConfigVariables.zoneMoveTimeOut = config.getInt("zoneMoveTimeOut", 10);
        ConfigVariables.startBoxX = config.getInt("startBoxX", 0);
        ConfigVariables.startBoxY = config.getInt("startBoxY", 200);
        ConfigVariables.startBoxZ = config.getInt("startBoxZ", 0);
        ConfigVariables.betterLogging = config.getBoolean("betterLogging");
    }

    public static void initCommands() {

        CommandTree commandTree = new CommandTree("battleRoyale");

        {
            commandTree.then(new LiteralArgument("start").withPermission(Permission.START_STOP.getPermission())

                    .executes((commandSender, commandArguments) -> {

                        new Start().execute(commandSender, commandArguments);
                    })
            );
        }

        {
            commandTree.then(new LiteralArgument("stop").withPermission(Permission.START_STOP.getPermission())

                    .executes((commandSender, commandArguments) -> {

                        new Stop().execute(commandSender, commandArguments);
                    })
            );
        }

        {
            commandTree.then(new LiteralArgument("startBox").withPermission(Permission.START_BOX.getPermission())

                    .then(new LiteralArgument("create").withPermission(Permission.START_BOX.getPermission())

                            .executes((commandSender, commandArguments) -> {

                                new Create().execute(commandSender, commandArguments);
                            })
                    )

                    .then(new LiteralArgument("remove").withPermission(Permission.START_BOX.getPermission())

                            .executes((commandSender, commandArguments) -> {

                                new Remove().execute(commandSender, commandArguments);
                            })
                    )
            );
        }

        {
            commandTree.then(new LiteralArgument("team").withPermission(Permission.TEAM_CREATE.getPermission())

                    .then(new LiteralArgument("create")

                            .then(new StringArgument("teamName").withPermission(Permission.TEAM_CREATE.getPermission())

                                    .executes((commandSender, commandArguments) -> {

                                        new ru.dvdishka.battleroyale.commands.team.Create().execute(commandSender, commandArguments);
                                    })
                            )
                    )

                    .then(new LiteralArgument("leave").withRequirement((commandSender) -> {

                        Logger.getLogger().devLog(String.valueOf(Team.getTeam(commandSender.getName()) != null));
                        return Team.getTeam(commandSender.getName()) != null;
                    })

                            .executes((commandSender, commandArguments) -> {

                                new Leave().execute(commandSender, commandArguments);
                            })
                    )

                    .then(new LiteralArgument("invite").withRequirement((commandSender) ->
                                    Team.getTeam(commandSender.getName()) != null &&
                                            Team.getTeam(commandSender.getName()).getLeader().equals(commandSender.getName()))

                            .then(new PlayerArgument("player")

                                    .executes(((commandSender, commandArguments) -> {

                                        new Invite().execute(commandSender, commandArguments);
                                    }))
                            )
                    )

                    .then(new LiteralArgument("list")

                            .executes((commandSender, commandArguments) -> {

                                new List().execute(commandSender, commandArguments);
                            })
                    )
            );
        }

        {
            commandTree.then(new LiteralArgument("accept")

                    .then(new StringArgument("team")

                            .executes((commandSender, commandArguments) -> {

                                new Accept().execute(commandSender, commandArguments);
                            })
                    )
            );
        }

        {
            commandTree.then(new LiteralArgument("cancel")

                    .then(new StringArgument("team")

                            .executes((commandSender, commandArguments) -> {

                                new Cancel().execute(commandSender, commandArguments);
                            })
                    )
            );
        }

        {
            commandTree.then(new LiteralArgument("revive").withPermission(Permission.REVIVE.getPermission())

                    .then(new PlayerArgument("player").withPermission(Permission.REVIVE.getPermission())

                            .executes((commandSender, commandArguments) -> {

                                new Revive().execute(commandSender, commandArguments);
                            })
                    )
            );
        }

        commandTree.register();
    }

    public static void initEventHandlers(Plugin plugin) {

        Bukkit.getPluginManager().registerEvents(new EventHandler(), plugin);
        Bukkit.getPluginManager().registerEvents(new ZoneStageHandler(), plugin);
    }

    public static void initRadar() {
        Radar.getInstance();
    }
}
