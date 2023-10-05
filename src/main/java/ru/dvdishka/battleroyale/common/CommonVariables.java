package ru.dvdishka.battleroyale.common;

import ru.dvdishka.battleroyale.classes.SuperPower;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

public class CommonVariables {

    public static Plugin plugin;
    public static final Logger logger = Bukkit.getLogger();

    public static boolean isFolia = false;

    public static boolean isGameStarted = false;
    public static int zoneStage = 0;
    public static BossBar timer = Bukkit.createBossBar("", BarColor.RED, BarStyle.SEGMENTED_10);
    public static int finalZoneX = 0;
    public static int finalZoneZ = 0;
    public static boolean isFinalZone = false;
    public static boolean isZoneMove = false;
    public static boolean isStartBox = false;

    public static HashSet<String> deadPlayers = new HashSet<>();
    public static HashSet<String> deadTeams = new HashSet<>();
    public static HashSet<String> players = new HashSet<>();
    public static HashMap<String, HashSet<String>> invites = new HashMap<>();
    public static HashMap<String, SuperPower> playersPower = new HashMap<>();

    public static void resetVariables() {

        CommonVariables.isGameStarted = false;
        CommonVariables.zoneStage = 0;
        CommonVariables.isFinalZone = false;
        CommonVariables.isZoneMove = false;
        CommonVariables.deadPlayers.clear();
        CommonVariables.players.clear();
        CommonVariables.deadTeams.clear();
        CommonVariables.playersPower.clear();

        for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            team.unregister();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (PotionEffect potionEffectType : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffectType.getType());
            }
        }
    }
}
