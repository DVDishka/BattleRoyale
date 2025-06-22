package ru.dvdishka.battleroyale.handlers.commands.game;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.Logger;
import ru.dvdishka.battleroyale.logic.common.Common;

public class StopCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        Common.resetVariables();

        Logger.getLogger().log("Battleroyale has been stopped");
        returnSuccess("Battleroyale has been stopped", sender);
    }
}
