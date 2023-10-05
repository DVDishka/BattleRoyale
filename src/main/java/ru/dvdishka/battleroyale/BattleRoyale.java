package ru.dvdishka.battleroyale;

import dvdishka.battleroyale.common.*;
import ru.dvdishka.battleroyale.handlers.EventHandler;
import ru.dvdishka.battleroyale.handlers.TabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import ru.dvdishka.battleroyale.common.CommonVariables;
import ru.dvdishka.battleroyale.common.ConfigVariables;
import ru.dvdishka.battleroyale.common.Initialization;
import ru.dvdishka.battleroyale.common.Scheduler;

import java.io.File;

public final class BattleRoyale extends JavaPlugin {

    @Override
    public void onEnable() {

        CommonVariables.plugin = this;

        Bukkit.setSpawnRadius(0);

        PluginCommand battleRoyaleCommand = getCommand("battleroyale");
        CommandExecutor commandExecutor = new ru.dvdishka.battleroyale.handlers.CommandExecutor();
        TabCompleter tabCompleter = new TabCompleter();
        Bukkit.getPluginManager().registerEvents(new EventHandler(), this);

        battleRoyaleCommand.setExecutor(commandExecutor);
        battleRoyaleCommand.setTabCompleter(tabCompleter);
        CommonVariables.timer.setVisible(false);

        if (!new File("plugins/BattleRoyale").exists()) {
            new File("plugins/BattleRoyale").mkdir();
        }
        if (!new File("plugins/BattleRoyale/config.yml").exists()) {
            CommonVariables.plugin.saveDefaultConfig();
        }

        Initialization.checkDependencies();
        Initialization.initConfig();

        CommonVariables.logger.info("BattleRoyale plugin has been enabled!");
    }

    @Override
    public void onDisable() {

        Scheduler.cancelTasks(this);

        CommonVariables.timer.setVisible(false);

        for (World world : Bukkit.getWorlds()) {
            world.setPVP(true);
            world.getWorldBorder().setCenter(0, 0);
            world.getWorldBorder().setSize(ConfigVariables.defaultWorldBorderDiameter);
        };

        CommonVariables.resetVariables();

        CommonVariables.logger.info("BattleRoyale plugin has been disabled");
    }
}
