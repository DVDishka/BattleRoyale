package ru.dvdishka.battleroyale.handlers.commands.team;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.Team;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;

public class CancelTeamCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        Common.buttonSound((Player) sender);

        if (Team.get((String) args.get("team")) == null) {

            returnFailure("Wrong team name!", sender);
            return;
        }

        Team team = Team.getTeam((String) args.get("team"));

        Team.invites.get((String) args.get("team")).remove(sender.getName());

        try {

            {
                Component header = Component.empty();
                Component text = Component.empty();

                header = header
                        .append(Component.text(team.getName())
                                .color(team.getColor())
                                .decorate(TextDecoration.BOLD));

                text = text
                        .append(Component.text(sender.getName())
                                .color(NamedTextColor.RED)
                                .decorate(TextDecoration.BOLD))
                        .append(Component.space())
                        .append(Component.text("declined your invitation"));

                Player leader = Bukkit.getPlayer(team.getLeader());

                Common.sendNotification(header, text, leader);
            }

        } catch (Exception ignored) {}
    }
}