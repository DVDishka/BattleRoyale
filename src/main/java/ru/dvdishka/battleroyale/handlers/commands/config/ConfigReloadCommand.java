package ru.dvdishka.battleroyale.handlers.commands.config;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.Initialization;
import ru.dvdishka.battleroyale.logic.Logger;
import ru.dvdishka.battleroyale.logic.common.ConfigVariables;
import ru.dvdishka.battleroyale.logic.common.PluginVariables;

import java.io.File;

public class ConfigReloadCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        send("Loading config...", sender);
        Initialization.initConfig(PluginVariables.plugin.getConfig());
        send("Config has been loaded", sender);

        send("Loading dropTypes config...", sender);
        Initialization.initDropTypes(new File(ConfigVariables.dropTypesFile));
        send("dropTypes config has been loaded", sender);

        send("Loading superpowers config...", sender);
        Initialization.initSuperPowers(new File(ConfigVariables.superPowersFile));
        send("superpowers config has been loaded", sender);
    }

    private void send(String message, CommandSender sender) {

        Logger.getLogger().log(message);
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(message);
        }
    }
}
