package ru.dvdishka.battleroyale.commands;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.classes.Team;
import ru.dvdishka.battleroyale.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.common.Common;

public class Revive implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        if (!sender.isOp()) {
            return;
        }

        Player revivePlayer = (Player) args.get("player");

        Common.deadPlayers.remove(revivePlayer.getName());
        try {
            Common.deadTeams.remove(Team.getTeam(revivePlayer.getName()).getName());

            returnSuccess(revivePlayer.getName() + " has been revived!", sender);
        } catch (Exception ignored) {}
    }
}
