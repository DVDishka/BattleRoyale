package ru.dvdishka.battleroyale.handlers.commands.team;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.logic.Team;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.Common;

public class InviteTeamCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        if (Common.isGameStarted) {

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

        returnSuccess(invitedPlayer.getName() + " has benn invited to your team!", sender);

        invitedPlayer.sendMessage(Component.text()
                .append(Component.text("You has been invited to ")
                        .color(TextColor.color(0, 143, 234))
                )
                .append(Component.text(inviterTeam.getName())
                        .color(inviterTeam.getColor())
                )
                .appendNewline()
                .append(Component.text("[ACCEPT]")
                        .color(TextColor.color(0, 234, 53))
                        .decorate(TextDecoration.BOLD)
                        .clickEvent(ClickEvent.runCommand("/battleroyale accept " + inviterTeam.getName()))
                )
                .appendSpace()
                .append(Component.text("[DECLINE]")
                        .color(TextColor.color(222, 16, 23))
                        .decorate(TextDecoration.BOLD)
                        .clickEvent(ClickEvent.runCommand("/battleroyale cancel " + inviterTeam.getName()))
                )
                .build()
        );
    }
}
