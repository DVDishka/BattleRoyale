package ru.dvdishka.battleroyale.logic.common;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerVariables {

    static HashSet<String> deadPlayers = new HashSet<>();
    static HashSet<String> players = new HashSet<>();
    static HashSet<String> reviveQueue = new HashSet<>();
    static HashSet<String> killQueue = new HashSet<>();



    public static boolean isDead(Player player) {
        return deadPlayers.contains(player.getName());
    }

    public static boolean isDead(String playerName) {
        return deadPlayers.contains(playerName);
    }

    public static void addDead(Player player) {
        deadPlayers.add(player.getName());
    }

    public static void addDead(String playerName) {
        deadPlayers.add(playerName);
    }

    public static void removeDead(Player player) {
        deadPlayers.remove(player.getName());
    }

    public static void removeDead(String playerName) {
        deadPlayers.remove(playerName);
    }



    public static boolean isBattleRoyalePlayer(Player player) {
        return players.contains(player.getName());
    }

    public static boolean isBattleRoyalePlayer(String playerName) {
        return players.contains(playerName);
    }

    public static boolean addBattleRoyalePlayer(Player player) {
        return players.add(player.getName());
    }

    public static boolean addBattleRoyalePlayer(String playerName) {
        return players.add(playerName);
    }

    public static List<Player> getOnlinePlayers() {
        List<Player> onlinePlayers = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isBattleRoyalePlayer(player)) {
                onlinePlayers.add(player);
            }
        }

        return onlinePlayers;
    }

    public static HashSet<String> getBattleRoyalePlayers() {

        return players;
    }



    public static void addKillQueue(Player player) {
        killQueue.add(player.getName());
    }

    public static void addKillQueue(String playerName) {
        killQueue.add(playerName);
    }

    public static void removeKillQueue(Player player) {
        killQueue.remove(player.getName());
    }

    public static void removeKillQueue(String playerName) {
        killQueue.remove(playerName);
    }

    public static boolean isKillQueue(Player player) {
        return killQueue.contains(player.getName());
    }

    public static boolean isKillQueue(String playerName) {
        return killQueue.contains(playerName);
    }



    public static void addReviveQueue(Player player) {
        reviveQueue.add(player.getName());
    }

    public static void addReviveQueue(String playerName) {
        reviveQueue.add(playerName);
    }

    public static void removeReviveQueue(Player player) {
        reviveQueue.remove(player.getName());
    }

    public static void removeReviveQueue(String playerName) {
        reviveQueue.remove(playerName);
    }

    public static boolean isReviveQueue(Player player) {
        return reviveQueue.contains(player.getName());
    }

    public static boolean isReviveQueue(String playerName) {
        return reviveQueue.contains(playerName);
    }
}
