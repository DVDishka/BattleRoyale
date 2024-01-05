package ru.dvdishka.battleroyale.logic.common;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class PluginVariables {

    public static final Logger logger = Bukkit.getLogger();
    public static Plugin plugin;
    public static World overWorld = Bukkit.getWorld(NamespacedKey.minecraft("overworld"));
    public static boolean isFolia = false;
}
