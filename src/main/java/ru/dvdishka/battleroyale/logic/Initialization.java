package ru.dvdishka.battleroyale.logic;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import ru.dvdishka.battleroyale.handlers.DropHandler;
import ru.dvdishka.battleroyale.handlers.commands.drop.*;
import ru.dvdishka.battleroyale.handlers.commands.ReviveCommand;
import ru.dvdishka.battleroyale.handlers.commands.StartCommand;
import ru.dvdishka.battleroyale.handlers.commands.StopCommand;
import ru.dvdishka.battleroyale.handlers.commands.common.Permission;
import ru.dvdishka.battleroyale.handlers.commands.startbox.CreateStartBoxCommand;
import ru.dvdishka.battleroyale.handlers.commands.startbox.RemoveStartBoxCommand;
import ru.dvdishka.battleroyale.handlers.EventHandler;
import ru.dvdishka.battleroyale.handlers.commands.team.*;
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainer;
import ru.dvdishka.battleroyale.logic.classes.drop.DropType;
import ru.dvdishka.battleroyale.handlers.ZoneStageHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class Initialization {

    public static void checkDependencies() {

        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
            Common.isFolia = true;
        } catch (Exception e) {
            Common.isFolia = false;
        }
    }

    public static void initConfig() {

        FileConfiguration config = Common.plugin.getConfig();

        ConfigVariables.defaultWorldBorderDiameter = (int) loadConfigValueSafely(config, "game.defaultWorldBorderRadius", ConfigVariables.defaultWorldBorderDiameter);

        ArrayList<Integer> zones = new ArrayList<>();
        for (int zoneRadius : config.getIntegerList("zones")) {
            zones.add(zoneRadius * 2);
        }
        ConfigVariables.zones = zones;

        ConfigVariables.times = config.getIntegerList("times");
        ConfigVariables.timeGameStart = config.getInt("timeGameStart");
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
        ConfigVariables.startBoxY = config.getInt("startBoxY",  200);

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

                                    .executesPlayer((commandSender, commandArguments) -> {

                                        new CreateTeamCommand().execute(commandSender, commandArguments);
                                    })
                            )
                    )

                    .then(new LiteralArgument("leave")

                            .executesPlayer((commandSender, commandArguments) -> {

                                new LeaveTeamCommand().execute(commandSender, commandArguments);
                            })
                    )

                    .then(new LiteralArgument("invite")

                            .then(new PlayerArgument("player")

                                    .executesPlayer(((commandSender, commandArguments) -> {

                                        new InviteTeamCommand().execute(commandSender, commandArguments);
                                    }))
                            )
                    )
            );
        }

        {
            commandTree.then(new LiteralArgument("accept")

                    .then(new StringArgument("team")

                            .executesPlayer((commandSender, commandArguments) -> {

                                new AcceptTeamCommand().execute(commandSender, commandArguments);
                            })
                    )
            );
        }

        {
            commandTree.then(new LiteralArgument("cancel")

                    .then(new StringArgument("team")

                            .executesPlayer((commandSender, commandArguments) -> {

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

                        new DropListCommand().execute(commandSender, commandArguments);
                    })

                    .then(new LiteralArgument("create").withPermission(Permission.DROP.getPermission())

                            .then(new StringArgument("dropTypeName")
                                    .includeSuggestions(ArgumentSuggestions.stringsWithTooltipsCollection(commandSenderSuggestionInfo -> {

                                        Collection<IStringTooltip> suggestions = new ArrayList<>();

                                        for (DropType dropType : DropType.getDropTypes()) {

                                            String tooltip = "";

                                            for (ItemStack itemStack : dropType.getItems()) {
                                                if (itemStack != null) {
                                                    tooltip = tooltip.concat(itemStack.getType().getKey().value() + ("(") + itemStack.getAmount() + ") ");
                                                }
                                            }

                                            suggestions.add(StringTooltip.ofString(dropType.getName(), tooltip));
                                        }

                                        return suggestions;
                                    }))
                                    .setOptional(true)

                                    .executes((commandSender, commandArguments) -> {

                                        new DropCreateCommand().execute(commandSender, commandArguments);
                                    })
                            )
                    )

                    .then(new LiteralArgument("delete").withPermission(Permission.DROP.getPermission())

                            .then(new TextArgument("dropName")
                                    .includeSuggestions(ArgumentSuggestions.stringCollection((commandSenderSuggestionInfo) -> {

                                        ArrayList<String> suggestions = new ArrayList<>();

                                        for (DropContainer dropContainer : DropContainer.getContainerList()) {

                                            suggestions.add("\"" + dropContainer.getName() + "\"");
                                        }

                                        return suggestions;
                                    }))
                                    .executes((commandSender, commandArguments) -> {

                                        new DropDeleteCommand().execute(commandSender, commandArguments);
                                    })
                            )
                    )

                    .then(new LiteralArgument("list")

                            .executes((commandSender, commandArguments) -> {

                                new DropListCommand().execute(commandSender, commandArguments);
                            })
                    )

                    .then(new LiteralArgument("follow")

                            .then(new TextArgument("dropName")
                                            .includeSuggestions(ArgumentSuggestions.stringCollection((commandSenderSuggestionInfo) -> {

                                ArrayList<String> suggestions = new ArrayList<>();

                                for (DropContainer dropContainer : DropContainer.getContainerList()) {

                                    suggestions.add("\"" + dropContainer.getName() + "\"");
                                }

                                return suggestions;
                            }))
                                            .executesPlayer((commandSender, commandArguments) -> {

                                                new DropFollowCommand().execute(commandSender, commandArguments);
                                            })
                            )
                    )

                    .then(new LiteralArgument("unfollow")

                            .executesPlayer((commandSender, commandArguments) -> {

                                new DropUnFollowCommand().execute(commandSender, commandArguments);
                            })
                    )
            );
        }

        commandTree.register();
    }

    public static void initDropTypes(File file) {

        FileConfiguration dropTypesConfig = YamlConfiguration.loadConfiguration(file);

        for (Map.Entry<String, Object> dropTypeMap : dropTypesConfig.getValues(false).entrySet()) {
            DropType.deserialize(dropTypesConfig.getConfigurationSection(dropTypeMap.getKey()), dropTypeMap.getKey());
        }
    }

    public static void initEventHandlers(Plugin plugin) {

        Bukkit.getPluginManager().registerEvents(new EventHandler(), plugin);
        Bukkit.getPluginManager().registerEvents(new ZoneStageHandler(), plugin);
        Bukkit.getPluginManager().registerEvents(new DropHandler(), plugin);
    }

    public static Object loadConfigValueSafely(FileConfiguration config, String path, Object value) {
        if (!config.contains(path)) {
            config.set(path, value);
        }
        return config.get(path);
    }
}
