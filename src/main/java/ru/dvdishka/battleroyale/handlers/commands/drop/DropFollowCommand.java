package ru.dvdishka.battleroyale.handlers.commands.drop;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainer;
import ru.dvdishka.battleroyale.ui.DropBar;

public class DropFollowCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        Common.buttonSound((Player) sender);

        if (!Common.isGameStarted) {
            returnFailure("You can not follow the drop container if the game is not running", sender);
            return;
        }

        DropContainer followedDropContainer = DropContainer.parseFromString((String) args.get("dropName"));

        if (followedDropContainer == null) {
            returnFailure("Wrong drop container name!", sender);
            return;
        }

        DropBar.getInstance((Player) sender).setInformation(followedDropContainer);

        returnSuccess("You have successfully followed the drop container", sender);
    }
}
