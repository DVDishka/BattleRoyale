package ru.dvdishka.battleroyale;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;
import ru.dvdishka.battleroyale.logic.common.Common;
import ru.dvdishka.battleroyale.logic.common.ConfigVariables;
import ru.dvdishka.battleroyale.logic.Initialization;
import ru.dvdishka.battleroyale.logic.Logger;
import ru.dvdishka.battleroyale.logic.common.PluginVariables;

import java.io.File;

public final class BattleRoyale extends JavaPlugin {

    @Override
    public void onEnable() {

        PluginVariables.plugin = this;

        for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            team.unregister();
        }

        if (!Bukkit.getPluginsFolder().toPath().resolve("BattleRoyale").toFile().exists()) {
            Bukkit.getPluginsFolder().toPath().resolve("BattleRoyale").toFile().mkdirs();
        }

        if (!Bukkit.getPluginsFolder().toPath().resolve("BattleRoyale").resolve("config.yml").toFile().exists()) {
            this.saveDefaultConfig();
        }

        Initialization.initConfig(this.getConfig());
        Initialization.checkDependencies();
        Initialization.initCommands();
        Initialization.initEventHandlers(this);

        if (!Bukkit.getPluginsFolder().toPath().resolve("BattleRoyale").resolve("dropTypes.yml").toFile().exists()) {
            this.saveResource("dropTypes.yml", false);
        }

        if (!Bukkit.getPluginsFolder().toPath().resolve("BattleRoyale").resolve("superpowers.yml").toFile().exists()) {
            this.saveResource("superpowers.yml", false);
        }

        if (!new File(ConfigVariables.dropTypesFile).exists()) {
            try {
                if (!new File(ConfigVariables.dropTypesFile).createNewFile()) {
                    Logger.getLogger().warn("Failed to create " + ConfigVariables.dropTypesFile);
                }
            } catch (Exception e) {
                Logger.getLogger().warn("Failed to create " + ConfigVariables.dropTypesFile);
            }
        }

        if (!new File(ConfigVariables.superPowersFile).exists()) {
            try {
                if (!new File(ConfigVariables.superPowersFile).createNewFile()) {
                    Logger.getLogger().warn("Failed to create " + ConfigVariables.superPowersFile);
                }
            } catch (Exception e) {
                Logger.getLogger().warn("Failed to create " + ConfigVariables.superPowersFile);
            }
        }

        Initialization.initDropTypes(new File(ConfigVariables.dropTypesFile));
        Initialization.initSuperPowers(new File(ConfigVariables.superPowersFile));

        CommandAPI.onEnable();

        PluginVariables.logger.info("BattleRoyale plugin has been enabled!");
    }

    public void onLoad() {

        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
    }

    @Override
    public void onDisable() {

        Common.resetVariables();

        CommandAPI.onDisable();

        PluginVariables.logger.info("BattleRoyale plugin has been disabled");
    }
}
