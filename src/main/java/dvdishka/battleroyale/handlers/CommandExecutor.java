package dvdishka.battleroyale.handlers;

import dvdishka.battleroyale.common.CommonVariables;
import dvdishka.battleroyale.common.ConfigVariables;
import dvdishka.battleroyale.common.SuperPowers;
import dvdishka.battleroyale.common.UpdateEvent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

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
            CommonVariables.isFinalZone = false;
            CommonVariables.isZoneMove = false;

            for (World world : Bukkit.getWorlds()) {

                world.setPVP(false);
                world.getWorldBorder().setCenter(0, 0);

                if (world.getWorldBorder().getSize()  != ConfigVariables.defaultWorldBorderDiametr) {

                    world.getWorldBorder().setSize(ConfigVariables.defaultWorldBorderDiametr, 1);

                } else {

                    world.getWorldBorder().setSize(ConfigVariables.defaultWorldBorderDiametr + 1, 1);
                }
            }

            for (Player player : Bukkit.getOnlinePlayers()) {

                player.setHealth(player.getMaxHealth());

                ArrayList<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());
                for (PotionEffect effect : effects) {
                    player.removePotionEffect(effect.getType());
                }

                CommonVariables.timer.addPlayer(player);

                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 10 , 1, false, false));
                int powerNumber = new Random().nextInt(0, SuperPowers.values().length);
                SuperPowers.values()[powerNumber].setToPlayer(player);

                Bukkit.getPluginManager().callEvent(new UpdateEvent());
            }

            return true;
        }

        if (commandName.equals("stop")) {

            return true;
        }

        return false;
    }
}
