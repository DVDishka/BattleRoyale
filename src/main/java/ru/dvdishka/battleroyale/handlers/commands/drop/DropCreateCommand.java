package ru.dvdishka.battleroyale.handlers.commands.drop;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.ConfigVariables;
import ru.dvdishka.battleroyale.logic.classes.drop.DropType;
import ru.dvdishka.battleroyale.logic.event.DropCreateEvent;

public class DropCreateCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        for (World world : ConfigVariables.dropSpawnWorlds) {
            Bukkit.getPluginManager().callEvent(new DropCreateEvent(DropType.getRandomType(), world));
        }

        returnSuccess("Drop has been spawned", sender);
    }
}
