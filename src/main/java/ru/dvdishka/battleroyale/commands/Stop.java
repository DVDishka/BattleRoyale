package ru.dvdishka.battleroyale.commands;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import ru.dvdishka.battleroyale.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.common.Common;
import ru.dvdishka.battleroyale.common.ConfigVariables;
import ru.dvdishka.battleroyale.common.Scheduler;

public class Stop implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        Scheduler.cancelTasks(Common.plugin);

        Common.timer.setVisible(false);

        for (World world : Bukkit.getWorlds()) {
            world.setPVP(true);
            world.getWorldBorder().setCenter(0, 0);
            world.getWorldBorder().setSize(ConfigVariables.defaultWorldBorderDiameter);
        }

        Common.resetVariables();
    }
}
