package ru.dvdishka.battleroyale.logic;

import ru.dvdishka.battleroyale.logic.common.ConfigVariables;
import ru.dvdishka.battleroyale.logic.common.PluginVariables;

public class Logger {

    public static Logger getLogger() {
        return new Logger();
    }

    public void log(String text) {
        PluginVariables.plugin.getLogger().info(text);
    }

    public void devLog(String text) {
        if (ConfigVariables.betterLogging) {
            PluginVariables.plugin.getLogger().info(text);
        }
    }

    public void warn(String text) {
        PluginVariables.plugin.getLogger().warning(text);
    }

    public void devWarn(String text) {
        if (ConfigVariables.betterLogging) {
            PluginVariables.plugin.getLogger().warning(text);
        }
    }
}
