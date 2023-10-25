package ru.dvdishka.battleroyale.commands.team;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.TitlePart;
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

        Team.invites.get(newTeamName).remove(newMemberName);

        newTeam.addMember(newMemberName);

        if (oldTeam != null) {

            oldTeam.removeMember(newMemberName);

            if (newMember != null) {

                returnFailure("You left " + oldTeam.getName(), sender);
            }
        }

        if (newMember != null) {

            returnSuccess("You joined " + newTeamName, sender);

        }

        for (String memberName : newTeam.getMembers()) {
            if (!memberName.equals(newMemberName)) {
                try {
                    Player memberPlayer = Bukkit.getPlayer(memberName);
                    memberPlayer.sendTitlePart(TitlePart.TITLE, Component
                            .text("Join")
                            .color(NamedTextColor.LIGHT_PURPLE));
                    memberPlayer.sendTitlePart(TitlePart.SUBTITLE, Component
                            .text(newMemberName)
                            .append(Component.space())
                            .append(Component.text("joined"))
                            .append(Component.space())
                            .append(Component.text(newTeamName)
                                    .color(newTeam.getColor())));
                } catch (Exception ignored) {}
            }
        }
    }
}
