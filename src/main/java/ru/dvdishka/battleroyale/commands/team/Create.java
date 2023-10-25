package ru.dvdishka.battleroyale.commands.team;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
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

        newTeam.addMember(sender.getName());

        returnSuccess("Team " + teamName + " has been created!", sender);
    }
}
