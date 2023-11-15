package ru.dvdishka.battleroyale;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.ConfigVariables;
import ru.dvdishka.battleroyale.logic.Initialization;
import ru.dvdishka.battleroyale.logic.Scheduler;
import ru.dvdishka.battleroyale.ui.Timer;

import java.io.File;

public final class BattleRoyale extends JavaPlugin {

    @Override
    public void onEnable() {

        Common.plugin = this;

        Bukkit.setSpawnRadius(0);

        Timer.getInstance().unregister();

        if (!new File("plugins/BattleRoyale").exists()) {
            new File("plugins/BattleRoyale").mkdir();
        }
        if (!new File("plugins/BattleRoyale/config.yml").exists()) {
            Common.plugin.saveDefaultConfig();
        }

        Initialization.checkDependencies();
        Initialization.initConfig();
        Initialization.initCommands();
        Initialization.initEventHandlers(this);
        Initialization.initRadar();
        Initialization.initDropTypes(new File("plugins/BattleRoyale"));

        CommandAPI.onEnable();

        Common.logger.info("BattleRoyale plugin has been enabled!");
    }

    public void onLoad() {

        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
    }

    @Override
    public void onDisable() {

        Scheduler.cancelTasks(this);

        Timer.getInstance().unregister();

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
