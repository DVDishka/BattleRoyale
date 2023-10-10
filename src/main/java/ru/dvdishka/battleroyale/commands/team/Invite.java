package ru.dvdishka.battleroyale.commands.team;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.classes.Team;
import ru.dvdishka.battleroyale.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.common.Common;

public class Invite implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        if (Common.isGameStarted) {

            returnFailure("You can not invite while the game is on!", sender);
            return;
        }

        Team playerTeam = Team.getTeam(sender.getName());

        if (playerTeam == null || !playerTeam.isLeader(sender.getName())) {
            returnFailure("You are not in the team or you are not the leader!", sender);
            return;
        }

        Player invitedPlayer = (Player) args.get("player");

        if (invitedPlayer == null) {
            returnFailure("There is no player with that name!", sender);
            return;
        }

        if (Team.getTeam(invitedPlayer) != null && Team.getTeam(invitedPlayer).getName().equals(playerTeam.getName())) {

            returnFailure("This player is already in your team!", sender);
            return;
        }

        Common.invites.get(playerTeam.getName()).add(invitedPlayer.getName());

        returnSuccess(invitedPlayer.getName() + " has benn invited to your team successfully!", sender);

        invitedPlayer.sendMessage(Component.text()
                .append(Component.text("You has been invited to ")
                        .color(TextColor.color(0, 143, 234))
                )
                .append(Component.text(playerTeam.getName())
                        .decorate(TextDecoration.BOLD)
                        .color(TextColor.color(201, 0, 238))
                )
                .appendNewline()
                .append(Component.text("[ACCEPT]")
                        .color(TextColor.color(0, 234, 53))
                        .decorate(TextDecoration.BOLD)
                        .clickEvent(ClickEvent.runCommand("/battleroyale accept " + playerTeam.getName()))
                )
                .appendSpace()
                .append(Component.text("[DECLINE]")
                        .color(TextColor.color(222, 16, 23))
                        .decorate(TextDecoration.BOLD)
                        .clickEvent(ClickEvent.runCommand("/battleroyale cancel " + playerTeam.getName()))
                )
                .build()
        );
    }
}
