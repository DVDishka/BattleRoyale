package dvdishka.battleroyale.common;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public class CommonVariables {

    public static Plugin plugin;
    public static final Logger logger = Bukkit.getLogger();

    public static boolean isGameStarted = false;
    public static int zoneStage = 0;
    public static BossBar timer = Bukkit.createBossBar("", BarColor.RED, BarStyle.SEGMENTED_10);
    public static int finalZoneX = 0;
    public static int finalZoneZ = 0;
    public static boolean isFinalZone = false;
    public static boolean isZoneMove = false;

    public static HashSet<String> deadPlayers = new HashSet<>();
    public static HashSet<String> players = new HashSet<>();
    public static HashMap<String, HashSet<String>> invites = new HashMap<>();
}
