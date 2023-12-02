package ru.dvdishka.battleroyale.handlers.commands.drop;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.ConfigVariables;
import ru.dvdishka.battleroyale.logic.classes.drop.DropType;
import ru.dvdishka.battleroyale.logic.event.DropCreateEvent;

public class DropCreateCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        if (!Common.isGameStarted) {
            returnFailure("Drop can not be spawned if the game is not running", sender);
            return;
        }

        if (DropType.getDropTypes().isEmpty()) {
            returnFailure("Drop types are not defined in " + "\"" + ConfigVariables.dropTypesFile + "\"", sender);
            return;
        }

        DropType dropType = DropType.getByNameOrRandom((String) args.get("dropTypeName"));

        for (World world : ConfigVariables.dropSpawnWorlds) {
            Bukkit.getPluginManager().callEvent(new DropCreateEvent(dropType, world));
        }
    }
}
