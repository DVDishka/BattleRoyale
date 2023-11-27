package ru.dvdishka.battleroyale.handlers.commands.drop;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainer;
import ru.dvdishka.battleroyale.ui.DropBar;

public class DropFollowCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        if (!Common.isGameStarted) {
            returnFailure("You can not follow the drop if the game is not running", sender);
            return;
        }

        String[] dropName = ((String) args.get("dropName")).split(" ");

        World world;

        try {
            world = Bukkit.getWorld(dropName[0]);
            if (world == null) {
                returnFailure("Wrong world name!", sender);
                return;
            }
        } catch (Exception e) {
            returnFailure("Wrong drop name!", sender);
            return;
        }

        int x = 0;
        try {
            x = Integer.parseInt(dropName[1]);
        } catch (Exception e) {
            returnFailure("Wrong X coordinate!", sender);
            return;
        }

        int y = 0;
        try {
            y = Integer.parseInt(dropName[2]);
        } catch (Exception e) {
            returnFailure("Wrong Y coordinate!", sender);
            return;
        }

        int z = 0;
        try {
            z = Integer.parseInt(dropName[3]);
        } catch (Exception e) {
            returnFailure("Wrong Z coordinate!", sender);
            return;
        }

        DropContainer followedDropContainer = DropContainer.getContainerByLocation(new Location(world, x, y, z));

        if (followedDropContainer == null) {
            returnFailure("Wrong drop name!", sender);
            return;
        }

        DropBar.getInstance((Player) sender).setInformation(followedDropContainer);

        Common.notificationSound((Player) sender);

        returnSuccess("You have successfully followed the drop", sender);
    }
}
