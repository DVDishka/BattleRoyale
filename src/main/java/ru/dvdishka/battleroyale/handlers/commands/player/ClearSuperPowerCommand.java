package ru.dvdishka.battleroyale.handlers.commands.player;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.classes.superpower.SuperPower;
import ru.dvdishka.battleroyale.logic.common.GameVariables;

public class ClearSuperPowerCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        if (!GameVariables.isGameStarted) {
            returnFailure("You can not change player's superpower while the game is not started", sender);
            return;
        }

        String playerName = (String) args.get("player");
        Player player = Bukkit.getPlayer(playerName);

        if (player == null) {
            returnFailure("This player is not online", sender);
            return;
        }

        if (SuperPower.getPlayerSuperPower(player) == null) {
            returnFailure("This player has no superpower", sender);
            return;
        }

        SuperPower.clearPlayerSuperPower(player);

        returnSuccess("Player's superpower has been removed successfully", sender);
        return;
    }
}
