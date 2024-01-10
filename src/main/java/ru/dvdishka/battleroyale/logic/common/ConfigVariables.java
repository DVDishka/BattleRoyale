package ru.dvdishka.battleroyale.logic.common;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.util.Arrays;
import java.util.List;

public class ConfigVariables {

    public static List<Integer> zones = Arrays.asList(1000, 800, 500, 200);
    public static List<Integer> times = Arrays.asList(300, 300, 300, 300);
    public static List<Integer> timeOuts = Arrays.asList(900, 450, 300, 120, 90);
    public static int timeGameStart = 1200;
    public static int maxTeamSize = 0;
    public static int pvpEnableZone = 1;
    public static int lastReviveZone = 1;
    public static int defaultWorldBorderDiameter = 1500;
    public static int finalZoneMoveDuration = 120;
    public static int minFinalZoneMove = 50;
    public static int maxFinalZoneMove = 100;
    public static int zoneMoveTimeOut = 10;
    public static int startBoxY = 200;
    public static List<World> dropSpawnWorlds = List.of(Bukkit.getWorld(NamespacedKey.minecraft("overworld")));
    public static int minDropSpawnY = 40;
    public static int maxDropSpawnY = 100;
    public static int dropOpenTime = 90;
    public static String dropTypesFile = "plugins/BattleRoyale/dropTypes.yml";
    public static String superPowersFile = "plugins/BattleRoyale/superpowers.yml";

    public static boolean betterLogging = false;
}
