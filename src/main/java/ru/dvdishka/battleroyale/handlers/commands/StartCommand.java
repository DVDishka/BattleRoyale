package ru.dvdishka.battleroyale.handlers.commands;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.dvdishka.battleroyale.logic.classes.superpower.SuperPower;
import ru.dvdishka.battleroyale.logic.event.NextGameStageEvent;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.ConfigVariables;
import ru.dvdishka.battleroyale.logic.Scheduler;
import ru.dvdishka.battleroyale.ui.Radar;
import ru.dvdishka.battleroyale.ui.Timer;
import ru.dvdishka.battleroyale.logic.classes.superpower.EffectUpdateTask;

import java.util.ArrayList;
import java.util.Random;

public class StartCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        Common.resetVariables();
        Common.isGameStarted = true;

        Timer.getInstance().register();

        Scheduler.getScheduler().runAsyncRepeatingTask(Common.plugin, (scheduledTask) -> {
            new EffectUpdateTask().run();
        }, 20, 20);

        for (World world : Bukkit.getWorlds()) {

            world.setPVP(false);
            world.getWorldBorder().setCenter(0, 0);

            world.getWorldBorder().setSize(ConfigVariables.defaultWorldBorderDiameter, 1);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {

            Common.players.add(player.getName());


            Scheduler.getScheduler().runPlayerTask(Common.plugin, player, (scheduledTask) -> {
                player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
            });

            ArrayList<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());
            for (PotionEffect effect : effects) {
                Scheduler.getScheduler().runPlayerTask(Common.plugin, player, (scheduledTask) -> {
                    player.removePotionEffect(effect.getType());
                });
            }

            Timer.getInstance().addViewer(player);
            Radar.getInstance().addViewer(player);

            Scheduler.getScheduler().runPlayerTask(Common.plugin, player, (scheduledTask) -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 200 , 1, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 1200, 9, false, false));
            });
            int powerNumber = new Random().nextInt(0, SuperPower.values().length);
            SuperPower.values()[powerNumber].setToPlayer(player);
        }

        Bukkit.getPluginManager().callEvent(new NextGameStageEvent());
    }
}
