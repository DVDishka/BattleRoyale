package ru.dvdishka.battleroyale.handlers.commands.drop;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.ui.DropBar;

public class DropUnFollowCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        DropBar.getInstance((Player) sender).setActive(false);

        returnSuccess("You have successfully unfollowed the drop", sender);
    }
}
