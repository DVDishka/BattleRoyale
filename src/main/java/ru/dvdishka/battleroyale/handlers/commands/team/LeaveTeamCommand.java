package ru.dvdishka.battleroyale.handlers.commands.team;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.logic.Team;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.Common;

public class LeaveTeamCommand implements CommandInterface {

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

            for (String memberName : playerTeam.getMembers()) {
                Player memberPlayer = Bukkit.getPlayer(memberName);
                try {
                    memberPlayer.sendTitlePart(TitlePart.TITLE, Component
                            .text("Unregistered")
                            .color(NamedTextColor.RED));
                    memberPlayer.sendTitlePart(TitlePart.SUBTITLE, Component
                            .text("Team")
                            .color(NamedTextColor.RED)
                            .append(Component.space())
                            .append(Component
                                    .text(playerTeam.getName())
                                    .color(playerTeam.getColor()))
                            .append(Component.space())
                            .append(Component
                                    .text("has been unregistered!")
                                    .color(NamedTextColor.RED)));
                } catch (Exception ignored) {}
            }

            playerTeam.unregister();
        }
        else {

            playerTeam.removeMember(sender.getName());

            for (String memberName : playerTeam.getMembers()) {
                Player memberPlayer = Bukkit.getPlayer(memberName);
                try {
                    memberPlayer.sendTitlePart(TitlePart.TITLE, Component
                            .text("Leave")
                            .color(NamedTextColor.RED));
                    memberPlayer.sendTitlePart(TitlePart.SUBTITLE, Component
                            .text(sender.getName())
                            .append(Component.space())
                            .append(Component
                                    .text("left your team!")
                                    .color(NamedTextColor.RED)));
                } catch (Exception ignored) {}
            }
        }

        returnSuccess("You left the team " + playerTeam.getName(), sender);
    }
}
