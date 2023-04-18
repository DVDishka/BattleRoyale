package dvdishka.battleroyale.handlers;

import dvdishka.battleroyale.classes.Team;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            if (sender.isOp()) {
                return List.of("start", "stop", "team", "startBox");
            } else {
                return List.of("team");
            }
        }

        if (args.length == 2) {
            if (args[0].equals("team") && Team.getTeam(sender.getName()) != null && Team.getTeam(sender.getName()).isLeader(sender.getName())) {
                return List.of("create", "leave", "invite", "list");
            }
            if (args[0].equals("team")) {
                if (Team.getTeam(sender.getName()) != null) {
                    return List.of("create", "leave", "list");
                } else {
                    return List.of("create", "list");
                }
            }
            if (args[0].equals("startBox") && sender.isOp()) {
                return List.of("create", "remove");
            }
        }

        if (args.length == 3) {
            if (args[0].equals("team") && Team.getTeam(sender.getName()) != null && Team.getTeam(sender.getName()).isLeader(sender.getName())) {
                return null;
            }
        }

        return List.of();
    }
}
