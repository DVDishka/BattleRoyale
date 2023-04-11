package dvdishka.battleroyale.classes;

import dvdishka.battleroyale.common.CommonVariables;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Team {

    private String name;
    private String leader;
    private TextColor color;
    private ArrayList<String> players = new ArrayList<>();
    public static ArrayList<Team> teams = new ArrayList<>();

    public Team(String name, String leader) {

        this.name = name;
        this.leader = leader;
        this.color = TextColor.color(new Random().nextInt(0, 255), new Random().nextInt(0, 255), new Random().nextInt(0, 255));
        teams.add(this);
        CommonVariables.invites.put(name, new HashSet<>());
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

            if (team.getPlayers().contains(player.getName())) {
                return team;
            }
        }
        return null;
    }

    public static Team getTeam(String name) {

        for (Team team : teams) {

            if (team.getPlayers().contains(name)) {
                return team;
            }
        }
        return null;
    }

    public TextColor getColor() {
        return this.color;
    }

    public void setColor(TextColor color) {
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

    public void setPlayers(ArrayList<String> players) {

        this.players = players;
    }

    public void addPlayer(String name) {
        Player player = Bukkit.getPlayer(name);
        player.displayName(Component.text(this.getName() + " " + name).color(this.color));
        players.add(name);
    }

    public void removePlayer(String name) {

        Player player = Bukkit.getPlayer(name);
        EntityScheduler playerScheduler = player.getScheduler();

        playerScheduler.run(CommonVariables.plugin, (task) -> {
            player.displayName(null);
            }, null);

        players.remove(name);
    }

    public boolean isMember(String name) {
        return players.contains(name);
    }

    public boolean isMember(Player player) {
        return players.contains(player.getName());
    }

    public boolean isLeader(String name) {
        return this.leader.equals(name);
    }

    public boolean isLeader(Player player) {
        return this.leader.equals(player.getName());
    }

    public ArrayList<String> getPlayers() {
        return players;
    }
}
