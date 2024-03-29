package ru.dvdishka.battleroyale.handlers.commands.team;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.logic.Team;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.common.Common;
import ru.dvdishka.battleroyale.logic.common.GameVariables;

public class CreateTeamCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        if (GameVariables.isGameStarted) {

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

        {
            Component header = Component.empty();
            Component text = Component.empty();

            header = header
                    .append(Component.text(newTeam.getName())
                            .color(newTeam.getColor())
                            .decorate(TextDecoration.BOLD));

            text = text
                    .append(Component.text("Team has been created"));

            Common.sendNotification(header, text, (Player) sender);
        }
    }
}
