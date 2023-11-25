package ru.dvdishka.battleroyale.handlers.commands;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.ConfigVariables;
import ru.dvdishka.battleroyale.logic.Scheduler;
import ru.dvdishka.battleroyale.ui.Timer;

public class StopCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        Common.resetVariables();
    }
}
