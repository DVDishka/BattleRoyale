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
import ru.dvdishka.battleroyale.logic.Common;

public class AcceptTeamCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        Common.buttonSound((Player) sender);

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

        if (Team.invites.get(newTeamName) == null || !Team.invites.get(newTeamName).contains(newMemberName)) {
            return;
        }

        Team.invites.get(newTeamName).remove(newMemberName);

        newTeam.addMember(newMemberName);

        if (oldTeam != null) {

            oldTeam.removeMember(newMemberName);

            {
                Component header = Component.empty();
                Component text = Component.empty();

                header = header
                        .append(Component.text(oldTeam.getName())
                                .color(oldTeam.getColor())
                                .decorate(TextDecoration.BOLD));

                text = text
                        .append(Component.text("You left the team")
                                .color(NamedTextColor.RED));

                Common.sendNotification(header, text, newMember);
            }

            for (String memberName : oldTeam.getMembers()) {

                try {

                    Player member = Bukkit.getPlayer(memberName);

                    {
                        Component header = Component.empty();
                        Component text = Component.empty();

                        header = header
                                .append(Component.text(oldTeam.getName())
                                        .color(oldTeam.getColor())
                                        .decorate(TextDecoration.BOLD));

                        text = text
                                .append(Component.text(newMemberName)
                                        .color(NamedTextColor.RED))
                                .append(Component.space())
                                .append(Component.text("left your team"));

                        Common.sendNotification(header, text, member);
                    }

                } catch (Exception ignored) {}
            }
        }

        {
            Component header = Component.empty();
            Component text = Component.empty();

            header = header
                    .append(Component.text(newTeamName)
                            .color(newTeam.getColor())
                            .decorate(TextDecoration.BOLD));

            text = text
                    .append(Component.text("You have joined the team"));

            Common.sendNotification(header, text, newMember);
        }

        for (String memberName : newTeam.getMembers()) {

            if (!memberName.equals(newMemberName)) {
                try {

                    Player member = Bukkit.getPlayer(memberName);

                    {
                        Component header = Component.empty();
                        Component text = Component.empty();

                        header = header
                                .append(Component.text(newTeamName)
                                        .color(newTeam.getColor())
                                        .decorate(TextDecoration.BOLD));

                        text = text
                                .append(Component.text(newMemberName))
                                .append(Component.space())
                                .append(Component.text("joined your team"));

                        Common.sendNotification(header, text, member);
                    }

                } catch (Exception ignored) {}
            }
        }
    }
}
