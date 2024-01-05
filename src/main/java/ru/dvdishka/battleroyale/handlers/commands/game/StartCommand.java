package ru.dvdishka.battleroyale.handlers.commands.game;

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
import ru.dvdishka.battleroyale.handlers.commands.startbox.CreateStartBoxCommand;
import ru.dvdishka.battleroyale.handlers.commands.startbox.OpenStartBoxCommand;
import ru.dvdishka.battleroyale.logic.Logger;
import ru.dvdishka.battleroyale.logic.classes.superpower.SuperPower;
import ru.dvdishka.battleroyale.logic.common.*;
import ru.dvdishka.battleroyale.logic.event.NextGameStageEvent;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.Scheduler;
import ru.dvdishka.battleroyale.ui.Radar;
import ru.dvdishka.battleroyale.ui.Timer;
import ru.dvdishka.battleroyale.logic.classes.superpower.EffectUpdateTask;

import java.util.ArrayList;

public class StartCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        if (GameVariables.isGameStarted) {
            returnFailure("You need to stop battleroyale to start it again \"/battleroyale admin stop\"", sender);
            return;
        }

        Common.resetVariables();
        GameVariables.isGameStarted = true;

        Timer.getInstance().register();
        Radar.getInstance().register();

        Scheduler.getScheduler().runAsyncRepeatingTask(PluginVariables.plugin, (scheduledTask) -> {
            new EffectUpdateTask().run();
        }, 20, 20);

        for (World world : Bukkit.getWorlds()) {

            world.setPVP(false);
            world.setTime(ConfigVariables.timeGameStart);
            world.getWorldBorder().setCenter(world.getSpawnLocation());
            world.getWorldBorder().setSize(ConfigVariables.defaultWorldBorderDiameter, 1);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {

            PlayerVariables.addBattleRoyalePlayer(player.getName());

            Scheduler.getScheduler().runPlayerTask(PluginVariables.plugin, player, (scheduledTask) -> {
                player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
            });

            ArrayList<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());
            for (PotionEffect effect : effects) {
                Scheduler.getScheduler().runPlayerTask(PluginVariables.plugin, player, (scheduledTask) -> {
                    player.removePotionEffect(effect.getType());
                });
            }

            Timer.getInstance().addViewer(player);
            Radar.getInstance().addViewer(player);

            Scheduler.getScheduler().runPlayerTask(PluginVariables.plugin, player, (scheduledTask) -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 100 , 1, false, false));
            });

            if (!SuperPower.isEmpty()) {
                SuperPower.getRandom().setToPlayer(player);
            }

            player.playSound(player, Sound.ITEM_GOAT_HORN_SOUND_0, 1000, 0);

            player.sendTitlePart(TitlePart.TITLE,
                    Component.text("Battleroyale")
                            .decorate(TextDecoration.BOLD)
                            .decorate(TextDecoration.UNDERLINED)
                            .color(NamedTextColor.GOLD));
            player.sendTitlePart(TitlePart.SUBTITLE,
                    Component.text("by DVDishka"));
        }

        StartElytraHandler.giveEveryoneStartElytra();

        new CreateStartBoxCommand().execute(sender, null);
        new OpenStartBoxCommand().execute(sender, null);

        Logger.getLogger().log("Battleroyale has been started");

        Bukkit.getPluginManager().callEvent(new NextGameStageEvent());
    }
}
