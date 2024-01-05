package ru.dvdishka.battleroyale.handlers.commands.player;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.common.GameVariables;
import ru.dvdishka.battleroyale.logic.common.PlayerVariables;
import ru.dvdishka.battleroyale.logic.event.game.GameDeathEvent;

public class KillCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        if (!GameVariables.isGameStarted) {
            returnFailure("You can not kill player while the game is not started", sender);
            return;
        }

        String killedPlayerName = (String) args.get("player");

        if (!PlayerVariables.isBattleRoyalePlayer(killedPlayerName)) {
            returnFailure("This player is not a battleroyale player", sender);
            return;
        }

        if (PlayerVariables.isDead(killedPlayerName)) {
            returnFailure("This player is already dead", sender);
            return;
        }

        if (Bukkit.getPlayer(killedPlayerName) == null) {
            PlayerVariables.addKillQueue(killedPlayerName);
        }

        Bukkit.getPluginManager().callEvent(new GameDeathEvent(killedPlayerName));
        returnSuccess(killedPlayerName + " has been killed successfully", sender);
    }
}
