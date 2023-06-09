package dvdishka.battleroyale;

import dvdishka.battleroyale.common.CommonVariables;
import dvdishka.battleroyale.common.ConfigVariables;
import dvdishka.battleroyale.common.Initialization;
import dvdishka.battleroyale.common.Scheduler;
import dvdishka.battleroyale.handlers.EventHandler;
import dvdishka.battleroyale.handlers.TabCompleter;
import dvdishka.battleroyale.tasks.endless.EffectUpdateTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class BattleRoyale extends JavaPlugin {

    @Override
    public void onEnable() {

        CommonVariables.plugin = this;

        Bukkit.setSpawnRadius(0);

        PluginCommand battleRoyaleCommand = getCommand("battleroyale");
        CommandExecutor commandExecutor = new dvdishka.battleroyale.handlers.CommandExecutor();
        TabCompleter tabCompleter = new TabCompleter();
        Bukkit.getPluginManager().registerEvents(new EventHandler(), this);

        battleRoyaleCommand.setExecutor(commandExecutor);
        battleRoyaleCommand.setTabCompleter(tabCompleter);
        CommonVariables.timer.setVisible(false);

        Initialization.checkDependencies();

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
