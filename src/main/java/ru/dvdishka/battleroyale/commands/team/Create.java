package ru.dvdishka.battleroyale.commands.team;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.classes.Team;
import ru.dvdishka.battleroyale.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.common.Common;

public class Create implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        if (Common.isGameStarted) {

            returnFailure("You can not create team while the game is on!", sender);
            return;
        }

        String teamName = (String) args.get("teamName");

        if (Team.getTeam(sender.getName()) != null) {
            returnFailure("You are already in the team!", sender);
            return;
        }

        for (Team team : Team.teams) {
            if (team.getName().equals(teamName)) {
                returnFailure("Team with that name already exist!", sender);
                return;
            }
        }

        Team newTeam = new Team(teamName, sender.getName());
        org.bukkit.scoreboard.Team newTeamScoreboard = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(teamName);

        newTeamScoreboard.setPrefix(teamName + " ");
        newTeamScoreboard.color(NamedTextColor.nearestTo(newTeam.getColor()));
        newTeamScoreboard.setAllowFriendlyFire(false);
        newTeamScoreboard.addPlayer((Player) sender);

        newTeam.addPlayer(sender.getName());

        returnSuccess("Team " + teamName + " has been created!", sender);
    }
}
