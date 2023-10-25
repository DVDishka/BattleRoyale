package ru.dvdishka.battleroyale.commands.team;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.classes.Team;
import ru.dvdishka.battleroyale.commands.common.CommandInterface;

public class List implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        TextComponent.Builder fullMessage = Component.text();

        fullMessage.append(Component.text("Teams:")
                .color(TextColor.color(187, 150, 0)));
        fullMessage.appendNewline();

        for (Team team : Team.teams) {

            Component teamMessagePart = Component.text("Team name: " + team.getName())
                    .color(team.getColor())
                    .appendNewline();
            teamMessagePart = teamMessagePart.append(Component.text("Members number: " + team.getMembers().size())
                    .color(team.getColor())
                    .appendNewline());
            teamMessagePart = teamMessagePart.append(Component.text("Members: ")
                    .color(team.getColor()));
            for (String member : team.getMembers()) {
                teamMessagePart = teamMessagePart.append(Component.text(member + " ")
                        .color(team.getColor()));
            }
            fullMessage.append(teamMessagePart);
            fullMessage.appendNewline();
            fullMessage.appendNewline();
        }

        fullMessage.append(Component.text("Solo: ")
                .color(TextColor.color(187, 50, 0)));
        fullMessage.appendNewline();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Team.getTeam(player) == null) {
                fullMessage.append(Component.text(player.getName() + " "));
            }
        }

        sender.sendMessage(fullMessage);
    }
}
