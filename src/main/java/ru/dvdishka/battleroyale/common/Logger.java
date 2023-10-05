package ru.dvdishka.battleroyale.common;

public class Logger {

    public static Logger getLogger() {
        return new Logger();
    }

    public void log(String text) {
        CommonVariables.plugin.getLogger().info(text);
    }

    public void devLog(String text) {
        if (ConfigVariables.betterLogging) {
            CommonVariables.plugin.getLogger().info(text);
        }
    }

    public void warn(String text) {
        CommonVariables.plugin.getLogger().warning(text);
    }

    public void devWarn(String text) {
        if (ConfigVariables.betterLogging) {
            CommonVariables.plugin.getLogger().warning(text);
        }
    }
}
