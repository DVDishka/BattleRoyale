package ru.dvdishka.battleroyale.classes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class Team {

    private String name;
    private String leader;
    private NamedTextColor color;
    private ArrayList<String> members = new ArrayList<>();
    private org.bukkit.scoreboard.Team scoreboardTeam;

    public static ArrayList<Team> teams = new ArrayList<>();
    public static HashSet<String> deadTeams = new HashSet<>();
    public static HashMap<String, HashSet<String>> invites = new HashMap<>();

    public Team(String name, String leader) {

        this.name = name;
        this.leader = leader;
        this.color = NamedTextColor.nearestTo(TextColor.color(new Random().nextInt(0, 255), new Random().nextInt(0, 255), new Random().nextInt(0, 255)));
        teams.add(this);
        this.scoreboardTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(name);

        scoreboardTeam.prefix(Component.text(name + " "));
        scoreboardTeam.color(this.color);
        scoreboardTeam.setAllowFriendlyFire(false);
        scoreboardTeam.addPlayer(Bukkit.getOfflinePlayer(leader));

        invites.put(name, new HashSet<>());
    }

    public static Team get(String teamName) {

        for (Team team : teams) {

            if (team.getName().equals(teamName)) {
                return team;
            }
        }

        return null;
    }

    public static Team getTeam(Player player) {

        for (Team team : teams) {

            if (team.getMembers().contains(player.getName())) {
                return team;
            }
        }
        return null;
    }

    public static Team getTeam(String name) {

        for (Team team : teams) {

            if (team.getMembers().contains(name)) {
                return team;
            }
        }
        return null;
    }

    public TextColor getColor() {
        return this.color;
    }

    public void setColor(NamedTextColor color) {
        this.color = color;
    }

    public String getLeader() {
        return this.leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public void addMember(String name) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        try {
            scoreboardTeam.addPlayer(player);
            members.add(name);
        } catch (Exception ignored) {}
    }

    public void removeMember(String name) {

        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        try {
            scoreboardTeam.removePlayer(player);
            members.remove(name);
        } catch (Exception ignored) {}
    }

    public boolean isMember(String name) {
        return members.contains(name);
    }

    public boolean isMember(Player player) {
        return members.contains(player.getName());
    }

    public boolean isLeader(String name) {
        return this.leader.equals(name);
    }

    public boolean isLeader(Player player) {
        return this.leader.equals(player.getName());
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public org.bukkit.scoreboard.Team getScoreboardTeam() {
        return scoreboardTeam;
    }

    public void unregister() {

        this.scoreboardTeam.unregister();
        this.members.clear();
        teams.remove(this);
    }
}
