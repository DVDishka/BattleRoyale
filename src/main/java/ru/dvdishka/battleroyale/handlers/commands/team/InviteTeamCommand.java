package ru.dvdishka.battleroyale.handlers.commands.team;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.logic.Team;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.common.Common;
import ru.dvdishka.battleroyale.logic.common.GameVariables;

public class InviteTeamCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        if (GameVariables.isGameStarted) {

            returnFailure("You can not invite while the game is on!", sender);
            return;
        }

        Team inviterTeam = Team.getTeam(sender.getName());

        if (inviterTeam == null || !inviterTeam.isLeader(sender.getName())) {
            returnFailure("You are not in the team or you are not the leader!", sender);
            return;
        }

        Player invitedPlayer = (Player) args.get("player");

        if (invitedPlayer == null) {
            returnFailure("There is no player with that name!", sender);
            return;
        }

        if (Team.getTeam(invitedPlayer) != null && Team.getTeam(invitedPlayer).getName().equals(inviterTeam.getName())) {
            returnFailure("This player is already in your team!", sender);
            return;
        }

        Team.invites.get(inviterTeam.getName()).add(invitedPlayer.getName());

        {
            Component header = Component.empty();
            Component text = Component.empty();

            header = header
                    .append(Component.text(inviterTeam.getName())
                            .color(inviterTeam.getColor())
                            .decorate(TextDecoration.BOLD));

            text = text
                    .append(Component.text(invitedPlayer.getName()))
                    .append(Component.space())
                    .append(Component.text("has been invited to your team!"));

            Common.sendNotification(header, text, (Player) sender);
        }

        {
            Component declineButton = Component.text("[DECLINE]")
                    .color(NamedTextColor.RED)
                    .decorate(TextDecoration.BOLD)
                    .clickEvent(ClickEvent.runCommand("/battleroyale cancel " + inviterTeam.getName()));

            Component acceptButton = Component.text("[ACCEPT]")
                    .color(NamedTextColor.GREEN)
                    .decorate(TextDecoration.BOLD)
                    .clickEvent(ClickEvent.runCommand("/battleroyale accept " + inviterTeam.getName()));

            Component header = Component.empty();
            Component text = Component.empty();

            header = header
                    .append(Component.text(inviterTeam.getName())
                            .color(inviterTeam.getColor())
                            .decorate(TextDecoration.BOLD));

            text = text
                    .append(Component.text(sender.getName()))
                    .append(Component.space())
                    .append(Component.text("invited you to "))
                    .append(Component.space())
                    .append(Component.text(inviterTeam.getName())
                            .color(inviterTeam.getColor()))
                    .append(Component.newline());

            text = text
                    .append(Component.text("-".repeat(27))
                            .color(NamedTextColor.YELLOW))
                    .append(Component.newline());

            text = text
                    .append(acceptButton)
                    .append(Component.space())
                    .append(declineButton)
                    .append(Component.newline());

            Common.sendNotification(header, text, invitedPlayer);
        }
    }
}
