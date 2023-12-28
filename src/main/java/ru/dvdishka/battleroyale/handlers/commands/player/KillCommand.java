package ru.dvdishka.battleroyale.handlers.commands.player;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.event.game.GameDeathEvent;

public class KillCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        String killedPlayer = (String) args.get("player");

        if (!Common.players.contains(killedPlayer)) {
            returnFailure("This player is not a battleroyale player", sender);
            return;
        }

        if (Common.deadPlayers.contains(killedPlayer)) {
            returnFailure("This player is already dead", sender);
            return;
        }

        Bukkit.getPluginManager().callEvent(new GameDeathEvent(killedPlayer));
        returnSuccess(killedPlayer + " has been killed successfully", sender);
    }
}
