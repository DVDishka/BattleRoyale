package ru.dvdishka.battleroyale.handlers.commands.drop;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainer;
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainerStage;
import ru.dvdishka.battleroyale.logic.common.Common;
import ru.dvdishka.battleroyale.logic.common.GameVariables;
import ru.dvdishka.battleroyale.ui.DropBar;

public class DropOpenCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        Common.buttonSound((Player) sender);

        if (!GameVariables.isGameStarted) {
            returnFailure("You can not open the drop container if the game is not running", sender);
            return;
        }

        DropContainer dropContainer = DropContainer.parseFromString((String) args.get("dropName"));

        if (dropContainer == null) {
            returnFailure("Wrong drop container name!", sender);
            return;
        }

        dropContainer.setStage(DropContainerStage.OPEN_STAGE);

        returnSuccess("Drop container has been opened", sender);
    }
}
