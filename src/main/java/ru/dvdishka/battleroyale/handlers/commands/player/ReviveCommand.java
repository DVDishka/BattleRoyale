package ru.dvdishka.battleroyale.handlers.commands.player;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.logic.Team;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.Common;

public class ReviveCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        String revivePlayerName = (String) args.get("player");

        if (!Common.deadPlayers.contains(revivePlayerName)) {
            returnFailure("This player is not dead", sender);
            return;
        }

        Common.deadPlayers.remove(revivePlayerName);

        for (String playerName : Common.players) {

            Common.sendNotification(
                    Component.text(playerName)
                            .decorate(TextDecoration.BOLD),
                    Component.text("Has been revived")
                            .color(NamedTextColor.GREEN),
                    Bukkit.getPlayer(playerName));
        }

        try {

            Team.deadTeams.remove(Team.getTeam(revivePlayerName).getName());

        } catch (Exception ignored) {}

        returnSuccess(revivePlayerName + " has been revived!", sender);
    }
}
