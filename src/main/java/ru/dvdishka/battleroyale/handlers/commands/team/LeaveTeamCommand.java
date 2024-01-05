package ru.dvdishka.battleroyale.handlers.commands.team;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.logic.Team;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.common.Common;
import ru.dvdishka.battleroyale.logic.common.GameVariables;

public class LeaveTeamCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        if (GameVariables.isGameStarted) {

            returnFailure("You can not leave while the game is on!", sender);
            return;
        }

        Team playerTeam = Team.getTeam(sender.getName());

        if (playerTeam == null) {
            returnFailure("You are not in the team!", sender);
            return;
        }

        // YOU LEFT THE TEAM MESSAGE
        {
            Component header = Component.empty();
            Component text = Component.empty();

            header = header
                    .append(Component.text(playerTeam.getName())
                            .color(playerTeam.getColor())
                            .decorate(TextDecoration.BOLD));

            text = text
                    .append(Component.text("You left the team")
                            .color(NamedTextColor.RED));

            Common.sendNotification(header, text, (Player) sender);
        }

        // TEAM DISBAND OR LEAVE MESSAGE
        {
            if (playerTeam.isLeader(sender.getName())) {

                for (String memberName : playerTeam.getMembers()) {

                    Player memberPlayer = Bukkit.getPlayer(memberName);

                    try {

                        Component header = Component.empty();
                        Component text = Component.empty();

                        header = header
                                .append(Component.text(playerTeam.getName())
                                        .color(playerTeam.getColor())
                                        .decorate(TextDecoration.BOLD));

                        text = text
                                .append(Component.text("The team has been disbanded")
                                        .color(NamedTextColor.RED))
                                .append(Component.newline());

                        Common.sendNotification(header, text, memberPlayer);

                    } catch (Exception ignored) {}
                }

                playerTeam.unregister();
            } else {

                playerTeam.removeMember(sender.getName());

                for (String memberName : playerTeam.getMembers()) {

                    Player memberPlayer = Bukkit.getPlayer(memberName);

                    try {

                        Component header = Component.empty();
                        Component text = Component.empty();

                        header = header
                                .append(Component.text(playerTeam.getName())
                                        .color(playerTeam.getColor())
                                        .decorate(TextDecoration.BOLD));

                        text = text
                                .append(Component.text(sender.getName())
                                        .decorate(TextDecoration.BOLD))
                                .append(Component.space())
                                .append(Component.text("left your team")
                                        .color(NamedTextColor.RED));

                        Common.sendNotification(header, text, memberPlayer);

                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }
}
