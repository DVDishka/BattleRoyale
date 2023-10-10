package ru.dvdishka.battleroyale.commands;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.classes.Team;
import ru.dvdishka.battleroyale.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.common.Common;

public class Accept implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        if (Common.isGameStarted) {

            returnFailure("You can not accept an invitation while the game is on!", sender);
            return;
        }

        String newMemberName = sender.getName();
        String newTeamName = (String) args.get("team");

        Player newMember = (Player) sender;

        Team oldTeam = Team.getTeam(newMemberName);
        Team newTeam = Team.get(newTeamName);

        if (newTeam == null) {

            returnFailure("Wrong team name!", sender);
            return;
        }

        Common.invites.get(newTeamName).remove(newMemberName);

        newTeam.addPlayer(newMemberName);
        Bukkit.getScoreboardManager().getMainScoreboard().getTeam(newTeam.getName()).addPlayer(Bukkit.getOfflinePlayer(newMemberName));

        if (oldTeam != null) {

            oldTeam.removePlayer(newMemberName);
            Bukkit.getScoreboardManager().getMainScoreboard().getTeam(oldTeam.getName()).removePlayer(Bukkit.getOfflinePlayer(newMemberName));

            if (newMember != null) {

                returnFailure("You left " + oldTeam.getName(), sender);
            }
        }

        if (newMember != null) {

            returnSuccess("You joined " + newTeamName, sender);

        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getName().equals(newMemberName)) {
                returnSuccess(newMemberName + " joined " + newTeamName + "!", player);
            }
        }
    }
}
