package dvdishka.battleroyale.handlers;

import dvdishka.battleroyale.common.CommonVariables;
import dvdishka.battleroyale.common.ConfigVariables;
import dvdishka.battleroyale.common.SuperPowers;
import dvdishka.battleroyale.common.UpdateEvent;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
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

        if (!command.getName().equals("battleroyale") || args.length < 1) {

            sender.sendMessage(ChatColor.RED + "Unknown command!");
            return false;
        }

        String commandName = args[0];

        if (commandName.equals("start")) {

            CommonVariables.isGameStarted = true;
            CommonVariables.zoneStage = 0;
            CommonVariables.isFinalZone = false;
            CommonVariables.isZoneMove = false;

            CommonVariables.timer.setVisible(true);

            for (World world : Bukkit.getWorlds()) {

                world.setPVP(false);
                world.getWorldBorder().setCenter(0, 0);

                if (world.getWorldBorder().getSize()  != ConfigVariables.defaultWorldBorderDiameter) {

                    world.getWorldBorder().setSize(ConfigVariables.defaultWorldBorderDiameter, 1);

                } else {

                    world.getWorldBorder().setSize(ConfigVariables.defaultWorldBorderDiameter + 1, 1);
                }
            }

            for (Player player : Bukkit.getOnlinePlayers()) {

                EntityScheduler playerScheduler = player.getScheduler();

                playerScheduler.run(CommonVariables.plugin, (task) -> {
                        player.setHealth(player.getMaxHealth());
                }, null);

                ArrayList<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());
                for (PotionEffect effect : effects) {
                    playerScheduler.run(CommonVariables.plugin, (task) -> {
                        player.removePotionEffect(effect.getType());
                    }, null);
                }

                CommonVariables.timer.addPlayer(player);

                playerScheduler.run(CommonVariables.plugin, (task) -> {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 10 , 1, false, false));
                }, null);
                int powerNumber = new Random().nextInt(0, SuperPowers.values().length);
                SuperPowers.values()[powerNumber].setToPlayer(player);

                Bukkit.getPluginManager().callEvent(new UpdateEvent());
            }

            return true;
        }

        if (commandName.equals("stop")) {

            Bukkit.getGlobalRegionScheduler().cancelTasks(CommonVariables.plugin);
            Bukkit.getAsyncScheduler().cancelTasks(CommonVariables.plugin);

            CommonVariables.timer.setVisible(false);

            for (World world : Bukkit.getWorlds()) {
                world.setPVP(true);
                world.getWorldBorder().setCenter(0, 0);
                world.getWorldBorder().setSize(ConfigVariables.defaultWorldBorderDiameter);
            }

            return true;
        }

        return false;
    }
}
