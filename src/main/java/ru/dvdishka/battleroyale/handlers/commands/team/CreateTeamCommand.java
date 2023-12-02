package ru.dvdishka.battleroyale.handlers.commands.team;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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

        Common.notificationSound((Player) sender);

        Component message = Component.empty();

        message = message
                .append(Component.newline())
                .append(Component.text("-".repeat(26))
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD))
                .append(Component.newline());

        message = message
                .append(Component.text(newTeam.getName())
                        .color(newTeam.getColor())
                        .decorate(TextDecoration.BOLD))
                .append(Component.newline());

        message = message
                .append(Component.text("-".repeat(27))
                        .color(NamedTextColor.YELLOW))
                .append(Component.newline());

        message = message
                .append(Component.text("Team has been created"))
                .append(Component.newline());

        message = message
                .append(Component.text("-".repeat(26))
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD))
                .append(Component.newline());

        sender.sendMessage(message);
    }
}
