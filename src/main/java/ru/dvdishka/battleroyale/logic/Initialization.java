package ru.dvdishka.battleroyale.logic;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import ru.dvdishka.battleroyale.handlers.DropHandler;
import ru.dvdishka.battleroyale.handlers.commands.drop.DropCreateCommand;
import ru.dvdishka.battleroyale.handlers.commands.ReviveCommand;
import ru.dvdishka.battleroyale.handlers.commands.StartCommand;
import ru.dvdishka.battleroyale.handlers.commands.StopCommand;
import ru.dvdishka.battleroyale.handlers.commands.common.Permission;
import ru.dvdishka.battleroyale.handlers.commands.drop.DropListCommand;
import ru.dvdishka.battleroyale.handlers.commands.startbox.CreateStartBoxCommand;
import ru.dvdishka.battleroyale.handlers.commands.startbox.RemoveStartBoxCommand;
import ru.dvdishka.battleroyale.handlers.EventHandler;
import ru.dvdishka.battleroyale.handlers.commands.team.*;
import ru.dvdishka.battleroyale.logic.classes.drop.DropType;
import ru.dvdishka.battleroyale.ui.Radar;
import ru.dvdishka.battleroyale.handlers.ZoneStageHandler;

import java.io.File;
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
        ConfigVariables.timeOuts = config.getIntegerList("timeOuts");
        ConfigVariables.pvpEnableZone = config.getInt("pvpEnableZone", 1);
        ConfigVariables.lastReviveZone = config.getInt("lastReviveZone", 1);
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

        ArrayList<World> dropSpawnWorlds = new ArrayList<>();
        for (String world : config.getStringList("dropSpawnWorlds")) {
            if (Bukkit.getWorld(world) != null) {
                dropSpawnWorlds.add(Bukkit.getWorld(world));
            } else {
                Logger.getLogger().warn("Incorrect world name in \"dropSpawnWorlds\" config.yml: " + world);
            }
        }
        ConfigVariables.dropSpawnWorlds = dropSpawnWorlds;

        ConfigVariables.minDropSpawnY = config.getInt("minDropSpawnY", 10);
        ConfigVariables.maxDropSpawnY = config.getInt("maxDropSpawnY", 256);

        ConfigVariables.dropOpenTime = config.getInt("dropOpenTime", 90);

        ConfigVariables.betterLogging = config.getBoolean("betterLogging");
    }

    public static void initCommands() {

        CommandTree commandTree = new CommandTree("battleRoyale");

        {
            commandTree.then(new LiteralArgument("start").withPermission(Permission.START_STOP.getPermission())

                    .executes((commandSender, commandArguments) -> {

                        new StartCommand().execute(commandSender, commandArguments);
                    })
            );
        }

        {
            commandTree.then(new LiteralArgument("stop").withPermission(Permission.START_STOP.getPermission())

                    .executes((commandSender, commandArguments) -> {

                        new StopCommand().execute(commandSender, commandArguments);
                    })
            );
        }

        {
            commandTree.then(new LiteralArgument("startBox").withPermission(Permission.START_BOX.getPermission())

                    .then(new LiteralArgument("create").withPermission(Permission.START_BOX.getPermission())

                            .executes((commandSender, commandArguments) -> {

                                new CreateStartBoxCommand().execute(commandSender, commandArguments);
                            })
                    )

                    .then(new LiteralArgument("remove").withPermission(Permission.START_BOX.getPermission())

                            .executes((commandSender, commandArguments) -> {

                                new RemoveStartBoxCommand().execute(commandSender, commandArguments);
                            })
                    )
            );
        }

        {
            commandTree.then(new LiteralArgument("team").withPermission(Permission.TEAM_CREATE.getPermission())

                    .then(new LiteralArgument("create")

                            .then(new StringArgument("teamName").withPermission(Permission.TEAM_CREATE.getPermission())

                                    .executes((commandSender, commandArguments) -> {

                                        new CreateTeamCommand().execute(commandSender, commandArguments);
                                    })
                            )
                    )

                    .then(new LiteralArgument("leave").withRequirement((commandSender) -> {

                        Logger.getLogger().devLog(String.valueOf(Team.getTeam(commandSender.getName()) != null));
                        return Team.getTeam(commandSender.getName()) != null;
                    })

                            .executes((commandSender, commandArguments) -> {

                                new LeaveTeamCommand().execute(commandSender, commandArguments);
                            })
                    )

                    .then(new LiteralArgument("invite").withRequirement((commandSender) ->
                                    Team.getTeam(commandSender.getName()) != null &&
                                            Team.getTeam(commandSender.getName()).getLeader().equals(commandSender.getName()))

                            .then(new PlayerArgument("player")

                                    .executes(((commandSender, commandArguments) -> {

                                        new InviteTeamCommand().execute(commandSender, commandArguments);
                                    }))
                            )
                    )

                    .then(new LiteralArgument("list")

                            .executes((commandSender, commandArguments) -> {

                                new ListTeamCommand().execute(commandSender, commandArguments);
                            })
                    )
            );
        }

        {
            commandTree.then(new LiteralArgument("accept")

                    .then(new StringArgument("team")

                            .executes((commandSender, commandArguments) -> {

                                new AcceptTeamCommand().execute(commandSender, commandArguments);
                            })
                    )
            );
        }

        {
            commandTree.then(new LiteralArgument("cancel")

                    .then(new StringArgument("team")

                            .executes((commandSender, commandArguments) -> {

                                new CancelTeamCommand().execute(commandSender, commandArguments);
                            })
                    )
            );
        }

        {
            commandTree.then(new LiteralArgument("revive").withPermission(Permission.REVIVE.getPermission())

                    .then(new PlayerArgument("player").withPermission(Permission.REVIVE.getPermission())

                            .executes((commandSender, commandArguments) -> {

                                new ReviveCommand().execute(commandSender, commandArguments);
                            })
                    )
            );
        }

        {
            commandTree.then(new LiteralArgument("drop")

                    .executes((commandSender, commandArguments) -> {

                        new DropListCommand();
                    })

                    .then(new LiteralArgument("create").withPermission(Permission.DROP_CREATE.getPermission())

                            .executes((commandSender, commandArguments) -> {

                                new DropCreateCommand().execute(commandSender, commandArguments);
                            })
                    )
            );
        }

        commandTree.register();
    }

    public static void initDropTypes(File file) {

        DropType.deserialize(file);
    }

    public static void initEventHandlers(Plugin plugin) {

        Bukkit.getPluginManager().registerEvents(new EventHandler(), plugin);
        Bukkit.getPluginManager().registerEvents(new ZoneStageHandler(), plugin);
        Bukkit.getPluginManager().registerEvents(new DropHandler(), plugin);
    }

    public static void initRadar() {
        Radar.getInstance();
    }
}
