package ru.dvdishka.battleroyale.handlers.commands.team;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import ru.dvdishka.battleroyale.logic.Team;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.Common;

public class CreateTeamCommand implements CommandInterface {

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
