package ru.dvdishka.battleroyale.handlers.commands;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.dvdishka.battleroyale.handlers.StartElytraHandler;
import ru.dvdishka.battleroyale.handlers.commands.startbox.RemoveStartBoxCommand;
import ru.dvdishka.battleroyale.logic.Logger;
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

        if (Common.isGameStarted) {
            returnFailure("You need to stop battleroyale to start it again \"/battleroyale stop\"", sender);
            return;
        }

        Common.resetVariables();
        Common.isGameStarted = true;

        Timer.getInstance().register();
        Radar.getInstance().register();

        Scheduler.getScheduler().runAsyncRepeatingTask(Common.plugin, (scheduledTask) -> {
            new EffectUpdateTask().run();
        }, 20, 20);

        for (World world : Bukkit.getWorlds()) {

            world.setPVP(false);
            world.setTime(ConfigVariables.timeGameStart);
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
            });

            int powerNumber = new Random().nextInt(0, SuperPower.values().length);
            SuperPower.values()[powerNumber].setToPlayer(player);

            player.playSound(player, Sound.ITEM_GOAT_HORN_SOUND_0, 1000, 0);

            player.sendTitlePart(TitlePart.TITLE,
                    Component.text("Battleroyale")
                            .decorate(TextDecoration.BOLD)
                            .decorate(TextDecoration.UNDERLINED)
                            .color(NamedTextColor.GOLD));
            player.sendTitlePart(TitlePart.SUBTITLE,
                    Component.text("by DVDishka"));
        }

        StartElytraHandler.giveStartElytra();
        new RemoveStartBoxCommand().execute(sender, null);

        Logger.getLogger().log("Battleroyale has been started");

        Bukkit.getPluginManager().callEvent(new NextGameStageEvent());
    }
}
