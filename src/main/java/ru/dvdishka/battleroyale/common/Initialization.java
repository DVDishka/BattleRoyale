package ru.dvdishka.battleroyale.common;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.configuration.file.FileConfiguration;
import ru.dvdishka.battleroyale.classes.Team;
import ru.dvdishka.battleroyale.commands.*;
import ru.dvdishka.battleroyale.commands.common.Permission;
import ru.dvdishka.battleroyale.commands.startbox.Create;
import ru.dvdishka.battleroyale.commands.startbox.Remove;
import ru.dvdishka.battleroyale.commands.team.Invite;
import ru.dvdishka.battleroyale.commands.team.Leave;
import ru.dvdishka.battleroyale.commands.team.List;

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

        ConfigVariables.defaultWorldBorderDiameter = config.getInt("defaultWorldBorderDiameter");
        ConfigVariables.zones = config.getIntegerList("zones");
        ConfigVariables.times = config.getIntegerList("times");
        ConfigVariables.timeOut = config.getInt("timeOut");
        ConfigVariables.finalZoneDuration = config.getInt("finalZoneDuration");
        ConfigVariables.finalZoneDiameter = config.getInt("finalZoneDiameter");
        ConfigVariables.finalZoneTimeOut = config.getInt("finalZoneTimeOut");
        ConfigVariables.finalZoneMoveDuration = config.getInt("finalZoneMoveDuration");
        ConfigVariables.minFinalZoneMove = config.getInt("minFinalZoneMove");
        ConfigVariables.maxFinalZoneMove = config.getInt("minFinalZoneMove");
        ConfigVariables.zoneMoveTimeOut = config.getInt("zoneMoveTimeOut");
        ConfigVariables.startBoxX = config.getInt("startBoxX");
        ConfigVariables.startBoxY = config.getInt("startBoxY");
        ConfigVariables.startBoxZ = config.getInt("startBoxZ");
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
}
