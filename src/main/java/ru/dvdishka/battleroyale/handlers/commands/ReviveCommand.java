package ru.dvdishka.battleroyale.handlers.commands;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.logic.Team;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.Common;

public class ReviveCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        if (!sender.isOp()) {
            return;
        }

        Player revivePlayer = (Player) args.get("player");

        Common.deadPlayers.remove(revivePlayer.getName());

        try {

            Team.deadTeams.remove(Team.getTeam(revivePlayer.getName()).getName());

            returnSuccess(revivePlayer.getName() + " has been revived!", sender);
        } catch (Exception ignored) {}
    }
}
