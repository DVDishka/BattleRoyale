package ru.dvdishka.battleroyale.commands;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import ru.dvdishka.battleroyale.classes.Team;
import ru.dvdishka.battleroyale.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.common.Common;

public class Cancel implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        if (Team.get((String) args.get("team")) == null) {

            returnFailure("Wrong team name!", sender);
            return;
        }

        Common.invites.get((String) args.get("team")).remove(sender.getName());

        try {
            returnFailure(sender.getName() + " declined your offer", Bukkit.getPlayer(Team.get((String) args.get("team")).getLeader()));
        } catch (Exception ignored) {}
    }
}