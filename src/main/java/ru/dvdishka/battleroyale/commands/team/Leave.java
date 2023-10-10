package ru.dvdishka.battleroyale.commands.team;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.classes.Team;
import ru.dvdishka.battleroyale.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.common.Common;
import ru.dvdishka.battleroyale.common.Scheduler;

import java.util.ArrayList;

public class Leave implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        if (Common.isGameStarted) {

            returnFailure("You can not leave while the game is on!", sender);
            return;
        }

        Team playerTeam = Team.getTeam(sender.getName());

        if (playerTeam == null) {
            returnFailure("You are not in the team!", sender);
            return;
        }

        if (playerTeam.isLeader(sender.getName())) {

            for (String member : playerTeam.getPlayers()) {

                Player player = Bukkit.getPlayer(member);

                Scheduler.getScheduler().runPlayerTask(Common.plugin, player, () -> {
                    player.displayName(null);
                });
            }

            playerTeam.setPlayers(new ArrayList<>());

            Team.teams.remove(playerTeam);

            Bukkit.getScoreboardManager().getMainScoreboard().getTeam(playerTeam.getName()).unregister();
        }
        else {
            playerTeam.removePlayer(sender.getName());
            Bukkit.getScoreboardManager().getMainScoreboard().getTeam(playerTeam.getName()).removePlayer(Bukkit.getOfflinePlayer(sender.getName()));
        }

        returnSuccess("You left the team " + playerTeam.getName(), sender);
    }
}
