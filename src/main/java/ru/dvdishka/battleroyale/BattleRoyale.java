package ru.dvdishka.battleroyale;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import ru.dvdishka.battleroyale.handlers.EventHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import ru.dvdishka.battleroyale.common.Common;
import ru.dvdishka.battleroyale.common.ConfigVariables;
import ru.dvdishka.battleroyale.common.Initialization;
import ru.dvdishka.battleroyale.common.Scheduler;

import java.io.File;

public final class BattleRoyale extends JavaPlugin {

    @Override
    public void onEnable() {

        Common.plugin = this;

        Bukkit.setSpawnRadius(0);

        Bukkit.getPluginManager().registerEvents(new EventHandler(), this);

        Common.timer.setVisible(false);

        if (!new File("plugins/BattleRoyale").exists()) {
            new File("plugins/BattleRoyale").mkdir();
        }
        if (!new File("plugins/BattleRoyale/config.yml").exists()) {
            Common.plugin.saveDefaultConfig();
        }

        Initialization.checkDependencies();
        Initialization.initConfig();
        Initialization.initCommands();

        CommandAPI.onEnable();

        Common.logger.info("BattleRoyale plugin has been enabled!");
    }

    public void onLoad() {

        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
    }

    @Override
    public void onDisable() {

        Scheduler.cancelTasks(this);

        Common.timer.setVisible(false);

        for (World world : Bukkit.getWorlds()) {
            world.setPVP(true);
            world.getWorldBorder().setCenter(0, 0);
            world.getWorldBorder().setSize(ConfigVariables.defaultWorldBorderDiameter);
        };

        Common.resetVariables();

        CommandAPI.onDisable();

        Common.logger.info("BattleRoyale plugin has been disabled");
    }
}
