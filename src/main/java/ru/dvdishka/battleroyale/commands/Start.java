package ru.dvdishka.battleroyale.commands;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.dvdishka.battleroyale.classes.SuperPower;
import ru.dvdishka.battleroyale.classes.NextGameStageEvent;
import ru.dvdishka.battleroyale.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.common.Common;
import ru.dvdishka.battleroyale.common.ConfigVariables;
import ru.dvdishka.battleroyale.common.Scheduler;
import ru.dvdishka.battleroyale.tasks.endless.EffectUpdateTask;

import java.util.ArrayList;
import java.util.Random;

public class Start implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        Common.isGameStarted = true;
        Common.zoneStage = 0;
        Common.isFinalZone = false;
        Common.isZoneMove = false;
        Common.deadPlayers.clear();
        Common.players.clear();

        Common.timer.setVisible(true);

        Scheduler.getScheduler().runAsyncRepeatingTask(Common.plugin, () -> {
            new EffectUpdateTask().run();
        }, 20, 20);

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

            Common.players.add(player.getName());


            Scheduler.getScheduler().runPlayerTask(Common.plugin, player, () -> {
                player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
            });

            ArrayList<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());
            for (PotionEffect effect : effects) {
                Scheduler.getScheduler().runPlayerTask(Common.plugin, player, () -> {
                    player.removePotionEffect(effect.getType());
                });
            }

            Common.timer.addPlayer(player);

            Scheduler.getScheduler().runPlayerTask(Common.plugin, player, () -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 200 , 1, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 1200, 9, false, false));
            });
            int powerNumber = new Random().nextInt(0, SuperPower.values().length);
            SuperPower.values()[powerNumber].setToPlayer(player);
        }

        Bukkit.getPluginManager().callEvent(new NextGameStageEvent());
    }
}
