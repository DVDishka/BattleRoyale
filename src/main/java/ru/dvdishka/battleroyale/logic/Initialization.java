package ru.dvdishka.battleroyale.logic;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import ru.dvdishka.battleroyale.handlers.*;
import ru.dvdishka.battleroyale.handlers.commands.drop.*;
import ru.dvdishka.battleroyale.handlers.commands.player.KillCommand;
import ru.dvdishka.battleroyale.handlers.commands.player.ReviveCommand;
import ru.dvdishka.battleroyale.handlers.commands.game.StartCommand;
import ru.dvdishka.battleroyale.handlers.commands.game.StopCommand;
import ru.dvdishka.battleroyale.handlers.commands.common.Permission;
import ru.dvdishka.battleroyale.handlers.commands.startbox.CreateStartBoxCommand;
import ru.dvdishka.battleroyale.handlers.commands.startbox.OpenStartBoxCommand;
import ru.dvdishka.battleroyale.handlers.commands.startbox.RemoveStartBoxCommand;
import ru.dvdishka.battleroyale.handlers.commands.team.*;
import ru.dvdishka.battleroyale.handlers.superpowerhandler.NoFallDamageHandler;
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainer;
import ru.dvdishka.battleroyale.logic.classes.drop.DropType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    public static void initConfig(FileConfiguration config) {

        // GAME SECTION
        {
            // DEFAULT ZONE RADIUS LOADING
            {
                int defaultWorldBorderRadius = loadIntConfigValueSafely(config, "game.defaultWorldBorderRadius", ConfigVariables.defaultWorldBorderDiameter / 2);

                if (defaultWorldBorderRadius <= 0) {
                    Logger.getLogger().warn("defaultWorldBorderRadius must be > 0. Using default value...");
                }
                else {
                    ConfigVariables.defaultWorldBorderDiameter = defaultWorldBorderRadius * 2;
                }
            }

            // ZONE RADIUS LIST LOADING
            {
                boolean isOrdered = true;
                boolean lessThenNull = false;
                boolean biggerThenDefaultRadius = false;
                int lastValue = 1000000000;

                ArrayList<Integer> zones = new ArrayList<>();
                for (int zoneRadius : loadIntListConfigValueSafely(config, "game.zones", ConfigVariables.zones)) {
                    if (lastValue <= zoneRadius) {
                        isOrdered = false;
                        break;
                    }
                    if (zoneRadius >= ConfigVariables.defaultWorldBorderDiameter) {
                        biggerThenDefaultRadius = true;
                        break;
                    }
                    if (zoneRadius <= 0) {
                        lessThenNull = true;
                        break;
                    }
                    zones.add(zoneRadius * 2);
                    lastValue = zoneRadius;
                }

                if (lessThenNull) {
                    if (ConfigVariables.zones.get(0) >= ConfigVariables.defaultWorldBorderDiameter) {
                        ConfigVariables.defaultWorldBorderDiameter = ConfigVariables.zones.get(0) * 2;
                    }
                    Logger.getLogger().warn("Each zone radius must be > 0. Using default value...");
                }
                else if (biggerThenDefaultRadius) {
                    if (ConfigVariables.zones.get(0) >= ConfigVariables.defaultWorldBorderDiameter) {
                        ConfigVariables.defaultWorldBorderDiameter = ConfigVariables.zones.get(0) * 2;
                    }
                    Logger.getLogger().warn("Each zone radius must be < defaultWorldBorderRadius. Using default value...");
                }
                else if (!isOrdered) {
                    if (ConfigVariables.zones.get(0) >= ConfigVariables.defaultWorldBorderDiameter) {
                        ConfigVariables.defaultWorldBorderDiameter = ConfigVariables.zones.get(0) * 2;
                    }
                    Logger.getLogger().warn("Zones must be ordered in descending order. Using default value...");
                }
                else if (zones.isEmpty()) {
                    if (ConfigVariables.zones.get(0) >= ConfigVariables.defaultWorldBorderDiameter) {
                        ConfigVariables.defaultWorldBorderDiameter = ConfigVariables.zones.get(0) * 2;
                    }
                    Logger.getLogger().warn("The list of zones must contain at least one element. Using default value...");
                }
                else {
                    ConfigVariables.zones = zones;
                }
            }

            // TIME LIST LOADING
            {
                boolean lessThenNull = false;

                ArrayList<Integer> times = new ArrayList<>();
                for (int time : loadIntListConfigValueSafely(config, "game.times", ConfigVariables.times)) {
                    if (time <= 0) {
                        lessThenNull = true;
                        break;
                    }
                    times.add(time);
                }

                if (lessThenNull) {
                    Logger.getLogger().warn("Each zone time must be > 0. Using default value...");
                    times.clear();
                    for (int i = ConfigVariables.zones.size() * 100; i >= 100; i -= 100) {
                        times.add(i);
                    }
                    ConfigVariables.times = times;
                }
                else if (times.size() > ConfigVariables.zones.size()) {
                    Logger.getLogger().warn("Amount of zone times must be equal to amount of zones. Cutting the time list...");
                    ConfigVariables.times = times.subList(0, ConfigVariables.zones.size());
                }
                else if (times.size() < ConfigVariables.zones.size()) {
                    Logger.getLogger().warn("Amount of zone times must be equal to amount of zones. Using default value...");
                    times.clear();
                    for (int i = ConfigVariables.zones.size() * 100; i >= 100; i -= 100) {
                        times.add(i);
                    }
                    ConfigVariables.times = times;
                }
                else {
                    ConfigVariables.times = times;
                }
            }

            // TIME OUT LIST LOADING
            {
                boolean lessThenNull = false;

                ArrayList<Integer> timeOuts = new ArrayList<>();
                for (int timeOut : loadIntListConfigValueSafely(config, "game.timeOuts", ConfigVariables.times)) {
                    if (timeOut <= 0) {
                        lessThenNull = true;
                        break;
                    }
                    timeOuts.add(timeOut);
                }

                if (lessThenNull) {
                    Logger.getLogger().warn("Each timeOut must be > 0. Using default value...");
                    timeOuts.clear();
                    for (int i = (ConfigVariables.zones.size() + 1) * 100; i >= 100; i -= 100) {
                        timeOuts.add(i);
                    }
                    ConfigVariables.timeOuts = timeOuts;
                }
                else if (timeOuts.size() > ConfigVariables.zones.size() + 1) {
                    Logger.getLogger().warn("Amount of timeOuts must be equal to amount of zones + 1. Cutting the timeOuts list...");
                    ConfigVariables.timeOuts = timeOuts.subList(0, ConfigVariables.zones.size() + 1);
                }
                else if (timeOuts.size() < ConfigVariables.zones.size() + 1) {
                    Logger.getLogger().warn("Amount of timeOuts must be equal to amount of zones + 1. Using default value...");
                    timeOuts.clear();
                    for (int i = (ConfigVariables.zones.size() + 1) * 100; i >= 100; i -= 100) {
                        timeOuts.add(i);
                    }
                    ConfigVariables.timeOuts = timeOuts;
                }
                else {
                    ConfigVariables.timeOuts = timeOuts;
                }
            }

            // PVP ENABLE ZONE LOADING
            {
                int pvpEnableZone = loadIntConfigValueSafely(config, "game.pvpEnableZone", ConfigVariables.pvpEnableZone);

                if (pvpEnableZone < 0) {
                    Logger.getLogger().warn("pvpEnableZone must be >= 0. Using default value...");
                }
                else if (pvpEnableZone > ConfigVariables.zones.size() + 1) {
                    Logger.getLogger().warn("pvpEnableZone must be <= amount of zones + 1. Using default value...");
                }
                else {
                    ConfigVariables.pvpEnableZone = pvpEnableZone;
                }
            }

            // LAST REVIVE ZONE LOADING
            {
                int lastReviveZone = loadIntConfigValueSafely(config, "game.lastReviveZone", ConfigVariables.lastReviveZone);

                if (lastReviveZone < 0) {
                    Logger.getLogger().warn("lastReviveZone must be >= 0. Using default value...");
                }
                else if (lastReviveZone > ConfigVariables.zones.size()) {
                    Logger.getLogger().warn("lastReviveZone must be <= amount of zones + 1. Using default value...");
                }
                else {
                    ConfigVariables.lastReviveZone = lastReviveZone;
                }
            }
        }

        // START SECTION
        {
            // TIME GAME START LOADING
            {
                int timeGameStart = loadIntConfigValueSafely(config, "start.timeGameStart", ConfigVariables.timeGameStart);

                if (timeGameStart < 0) {
                    Logger.getLogger().warn("timeGameStart must be >= 0. Using default value...");
                } else {
                    ConfigVariables.timeGameStart = timeGameStart;
                }
            }
        }

        // DROP SECTION
        {
            // DROP TYPES FILE LOADING
            {
                ConfigVariables.dropTypesFile = loadStringConfigValueSafely(config, "drop.dropTypesFile", ConfigVariables.dropTypesFile);
            }

            // DROP OPEN TIME
            {
                int dropOpenTime = loadIntConfigValueSafely(config, "drop.dropOpenTime", ConfigVariables.dropOpenTime);

                if (dropOpenTime < 0) {
                    Logger.getLogger().warn("dropOpenTime must be >= 0. Using default value...");
                }
                else {
                    ConfigVariables.dropOpenTime = dropOpenTime;
                }
            }

            // DROP SPAWN WORLDS
            {
                List<String> worldNames = new ArrayList<>();
                for (World world : ConfigVariables.dropSpawnWorlds) {
                    worldNames.add(world.getName());
                }

                List<String> dropSpawnWorlds = loadStringListConfigValueSafely(config, "drop.dropSpawnWorlds", worldNames);
                List<World> worlds = new ArrayList<>();

                for (String worldName : dropSpawnWorlds) {
                    if (Bukkit.getWorld(NamespacedKey.minecraft(worldName)) != null) {
                        worlds.add(Bukkit.getWorld(NamespacedKey.minecraft(worldName)));
                    } else if (Bukkit.getWorld(NamespacedKey.fromString(worldName)) != null) {
                        worlds.add(Bukkit.getWorld(NamespacedKey.fromString(worldName)));
                    } else {
                        Logger.getLogger().warn("Wrong dropSpawnWorld \"" + worldName + "\"");
                    }
                }

                ConfigVariables.dropSpawnWorlds = worlds;
            }
        }

        // FINAL ZONE SECTION
        {
            // FINAL ZONE MOVE DURATION LOADING
            {
                int finalZoneMoveDuration = loadIntConfigValueSafely(config, "finalZone.finalZoneMoveDuration", ConfigVariables.finalZoneMoveDuration);

                if (finalZoneMoveDuration < 1) {
                    Logger.getLogger().warn("finalZoneMoveDuration must be > 0. Using default value...");
                }
                else {
                    ConfigVariables.finalZoneMoveDuration = finalZoneMoveDuration;
                }
            }

            // MIN FINAL ZONE MOVE LOADING
            {
                int minFinalZoneMove = loadIntConfigValueSafely(config, "finalZone.minFinalZoneMove", ConfigVariables.minFinalZoneMove);

                if (minFinalZoneMove < 1) {
                    Logger.getLogger().warn("minFinalZoneMove must be > 0. Using default value...");
                }
                else {
                    ConfigVariables.minFinalZoneMove = minFinalZoneMove;
                }
            }

            // MAX FINAL ZONE MOVE LOADING
            {
                int maxFinalZoneMove = loadIntConfigValueSafely(config, "finalZone.maxFinalZoneMove", ConfigVariables.maxFinalZoneMove);

                if (maxFinalZoneMove < 1) {
                    Logger.getLogger().warn("maxFinalZoneMove must be > 0. Using default value...");
                }
                else if (maxFinalZoneMove < ConfigVariables.maxFinalZoneMove) {
                    ConfigVariables.maxFinalZoneMove = ConfigVariables.minFinalZoneMove;
                    Logger.getLogger().warn("maxFinalZoneMove must be >= minFinalZoneMove. Using default value...");
                }
                else {
                    ConfigVariables.maxFinalZoneMove = maxFinalZoneMove;
                }
            }

            // ZONE MOVE TIMEOUT LOADING
            {
                int zoneMoveTimeOut = loadIntConfigValueSafely(config, "finalZone.zoneMoveTimeOut", ConfigVariables.zoneMoveTimeOut);

                if (zoneMoveTimeOut < 1) {
                    Logger.getLogger().warn("zoneMoveTimeOut must be > 0. Using default value...");
                }
                else {
                    ConfigVariables.zoneMoveTimeOut = zoneMoveTimeOut;
                }
            }

            ConfigVariables.betterLogging = loadBoolConfigValueSafely(config, "betterLogging", ConfigVariables.betterLogging);
        }
    }

    public static void initCommands() {

        CommandTree commandTree = new CommandTree("battleRoyale");

        // TEAM
        {
            commandTree.then(new LiteralArgument("team")

                    .then(new LiteralArgument("create")

                            .then(new StringArgument("teamName")

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

        // ACCEPT OR CANCEL
        {
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
        }

        {
            commandTree.then(new LiteralArgument("revive").withPermission(Permission.PLAYER_EDIT.getPermission())

                    .then(new PlayerArgument("player")

                            .executes((commandSender, commandArguments) -> {

                                new ReviveCommand().execute(commandSender, commandArguments);
                            })
                    )
            );
        }

        // DROP
        {
            commandTree.then(new LiteralArgument("drop")

                    .executes((commandSender, commandArguments) -> {

                        new DropListCommand().execute(commandSender, commandArguments);
                    })

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

        // ADMIN
        {
            commandTree.then(new LiteralArgument("admin")

                    // GAME
                    .then(new LiteralArgument("game").withPermission(Permission.START_STOP.getPermission())

                            .then(new LiteralArgument("start")

                                    .executes((commandSender, commandArguments) -> {

                                        new StartCommand().execute(commandSender, commandArguments);
                                    })
                            )

                            .then(new LiteralArgument("stop")

                                    .executes((commandSender, commandArguments) -> {

                                        new StopCommand().execute(commandSender, commandArguments);
                                    })
                            )
                    )

                    // START BOX
                    .then(new LiteralArgument("startBox").withPermission(Permission.START_BOX.getPermission())

                            .then(new LiteralArgument("create")

                                    .executes((commandSender, commandArguments) -> {

                                        new CreateStartBoxCommand().execute(commandSender, commandArguments);
                                    })
                            )

                            .then(new LiteralArgument("remove")

                                    .executes((commandSender, commandArguments) -> {

                                        new RemoveStartBoxCommand().execute(commandSender, commandArguments);
                                    })
                            )

                            .then(new LiteralArgument("open")

                                    .executes((commandSender, commandArguments) -> {

                                        new OpenStartBoxCommand().execute(commandSender, commandArguments);
                                    })
                            )
                    )

                    .then(new LiteralArgument("drop").withPermission(Permission.DROP_EDIT.getPermission())

                            .then(new LiteralArgument("create")

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

                            .then(new LiteralArgument("delete")

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
                    )

                    .then(new LiteralArgument("player").setListed(false).withPermission(Permission.PLAYER_EDIT.getPermission())

                        .then(new LiteralArgument("revive")

                                .then(new StringArgument("player")

                                        .replaceSuggestions(ArgumentSuggestions.stringCollection((commandSenderSuggestionInfo) -> {

                                            ArrayList<String> deadPlayers = new ArrayList<>();

                                            for (String playerName : Common.players) {
                                                if (Common.deadPlayers.contains(playerName)) {
                                                    deadPlayers.add(playerName);
                                                }
                                            }

                                            return deadPlayers;
                                        }))

                                        .executes((commandSender, commandArguments) -> {

                                            new ReviveCommand().execute(commandSender, commandArguments);
                                        })
                                )
                        )

                        .then(new LiteralArgument("kill")

                                .then(new StringArgument("player")

                                        .replaceSuggestions(ArgumentSuggestions.stringCollection((commandSenderSuggestionInfo) -> {

                                            ArrayList<String> alivePlayers = new ArrayList<>();

                                            for (String playerName : Common.players) {
                                                if (!Common.deadPlayers.contains(playerName)) {
                                                    alivePlayers.add(playerName);
                                                }
                                            }

                                            return alivePlayers;
                                        }))

                                        .executes((commandSender, commandArguments) -> {

                                            new KillCommand().execute(commandSender, commandArguments);
                                        })
                                )
                        )
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
        Bukkit.getPluginManager().registerEvents(new StartElytraHandler(), plugin);
        Bukkit.getPluginManager().registerEvents(new GameHandler(), plugin);
        Bukkit.getPluginManager().registerEvents(new NoFallDamageHandler(), plugin);
    }

    public static int loadIntConfigValueSafely(FileConfiguration config, String path, int defaultValue) {
        if (!config.contains(path)) {
            Logger.getLogger().warn("Failed to load " + path + " from BattleRoyale config file. Using default value...");
            config.set(path, defaultValue);
            Common.plugin.saveConfig();
        }
        return config.getInt(path, defaultValue);
    }

    public static String loadStringConfigValueSafely(FileConfiguration config, String path, String defaultValue) {
        if (!config.contains(path)) {
            Logger.getLogger().warn("Failed to load " + path + " from BattleRoyale config file. Using default value...");
            config.set(path, defaultValue);
            Common.plugin.saveConfig();
        }
        return config.getString(path, defaultValue);
    }

    public static boolean loadBoolConfigValueSafely(FileConfiguration config, String path, boolean defaultValue) {
        if (!config.contains(path)) {
            Logger.getLogger().warn("Failed to load " + path + " from BattleRoyale config file. Using default value...");
            config.set(path, defaultValue);
            Common.plugin.saveConfig();
        }
        return config.getBoolean(path, defaultValue);
    }

    public static List<Integer> loadIntListConfigValueSafely(FileConfiguration config, String path, List<Integer> defaultValue) {
        if (!config.contains(path)) {
            Logger.getLogger().warn("Failed to load " + path + " from BattleRoyale config file. Using default value...");
            config.set(path, defaultValue);
            Common.plugin.saveConfig();
        }
        return config.getIntegerList(path);
    }

    public static List<String> loadStringListConfigValueSafely(FileConfiguration config, String path, List<String> defaultValue) {
        if (!config.contains(path)) {
            Logger.getLogger().warn("Failed to load " + path + " from BattleRoyale config file. Using default value...");
            config.set(path, defaultValue);
            Common.plugin.saveConfig();
        }
        return config.getStringList(path);
    }
}
