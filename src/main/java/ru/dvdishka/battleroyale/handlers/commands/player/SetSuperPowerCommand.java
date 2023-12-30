package ru.dvdishka.battleroyale.handlers.commands.player;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.classes.superpower.SuperPower;

public class SetSuperPowerCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        if (!Common.isGameStarted) {
            returnFailure("You can not change player's superpower while the game is not started", sender);
            return;
        }

        String playerName = (String) args.get("player");
        Player player = Bukkit.getPlayer(playerName);

        SuperPower superPower = SuperPower.getByName((String) args.get("superpower"));

        if (superPower == null) {
            returnFailure("There is no superpower with that name", sender);
            return;
        }

        if (player == null) {
            returnFailure("This player is not online", sender);
            return;
        }

        SuperPower.clearPlayerSuperPower(player);
        superPower.setToPlayer(player);

        returnSuccess("Superpower has been set to the player successfully", sender);
        return;
    }
}
