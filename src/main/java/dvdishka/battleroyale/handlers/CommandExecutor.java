package dvdishka.battleroyale.handlers;

import dvdishka.battleroyale.common.CommonVariables;
import dvdishka.battleroyale.common.ConfigVariables;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandExecutor implements org.bukkit.command.CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!command.getName().equals("battleroyale") || args.length != 1) {

            sender.sendMessage(ChatColor.RED + "Unknown command!");
            return false;
        }

        String commandName = args[0];

        if (commandName.equals("start")) {

            CommonVariables.isGameStarted = true;
            CommonVariables.zoneStage = 0;

            for (World world : Bukkit.getWorlds()) {

                world.setPVP(false);

                if (world.getWorldBorder().getSize()  != ConfigVariables.defaultWorldBorderDiametr) {

                    world.getWorldBorder().setSize(ConfigVariables.defaultWorldBorderDiametr, 1);

                } else {

                    world.getWorldBorder().setSize(ConfigVariables.defaultWorldBorderDiametr + 1, 1);
                }
            }

            for (Player player : Bukkit.getOnlinePlayers()) {

                CommonVariables.timer.addPlayer(player);
            }

            return true;
        }

        if (commandName.equals("stop")) {

            return true;
        }

        return false;
    }
}
