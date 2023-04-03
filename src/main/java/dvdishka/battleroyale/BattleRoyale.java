package dvdishka.battleroyale;

import dvdishka.battleroyale.common.CommonVariables;
import dvdishka.battleroyale.handlers.EventHandler;
import dvdishka.battleroyale.handlers.TabCompleter;
import org.bukkit.Bukkit;
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

        CommonVariables.logger.info("BattleRoyale plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        CommonVariables.logger.info("BattleRoyale plugin has been disabled");
    }
}
