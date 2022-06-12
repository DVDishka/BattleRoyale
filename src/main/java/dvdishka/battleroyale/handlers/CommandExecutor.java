package dvdishka.battleroyale.handlers;

import dvdishka.battleroyale.common.CommonVariables;
import dvdishka.battleroyale.threads.PlayerUpdater;
import dvdishka.battleroyale.threads.Logic;
import dvdishka.battleroyale.threads.Tasks.GiveSuperPower;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class CommandExecutor implements org.bukkit.command.CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!command.getName().equals("battleroyale") || args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Unknown command!");
            return false;
        }

        Player player = Bukkit.getPlayer(sender.getName());
        String commandName = args[0];

        if (commandName.equals("start")) {
            BossBar bossBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SEGMENTED_10);
            Thread logic = new Logic(bossBar, "Logic");
            Thread playerUpdater = new PlayerUpdater(bossBar, "playerUpdater");
            logic.start();
            playerUpdater.start();
            WorldBorder worldBorder = Bukkit.getWorld("world").getWorldBorder();
            worldBorder.setCenter(0, 0);
            worldBorder.setSize(100000);
            worldBorder.setSize(CommonVariables.zones.get(0), CommonVariables.times.get(0));
            CommonVariables.setZoneStage(1);
            BukkitScheduler scheduler = Bukkit.getScheduler();
            GiveSuperPower.Execute();
            for (World world : Bukkit.getWorlds()) {
                world.setPVP(false);
            }
            int task = scheduler.scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("BattleRoyale"), new Runnable() {
                @Override
                public void run() {
                    if (CommonVariables.getZoneStage() != CommonVariables.getExecutedZoneStage() && CommonVariables.getZoneStage() <= CommonVariables.zones.size()) {
                        CommonVariables.setExecutedZoneStage(CommonVariables.getZoneStage());
                        for (World world : Bukkit.getWorlds()) {
                            world.getWorldBorder().setSize(CommonVariables.zones.get(CommonVariables.getZoneStage() - 1)
                                    , CommonVariables.times.get(CommonVariables.getZoneStage() - 1));
                            if (CommonVariables.getZoneStage() == 2) {
                                world.setPVP(true);
                            }
                        }
                    }
                    if (CommonVariables.getZoneStage() == CommonVariables.zones.size() + 1) {
                        for (World world : Bukkit.getWorlds()) {
                            double oldSize = world.getWorldBorder().getSize();
                            world.getWorldBorder().setSize(Math.abs(CommonVariables.getFinalZoneCenter() + oldSize));
                            world.getWorldBorder().setCenter(CommonVariables.getFinalZoneCenter(),
                                    CommonVariables.getFinalZoneCenter());
                            world.getWorldBorder().setSize(2, CommonVariables.finalZoneTime);
                        }
                        scheduler.cancelTasks(Bukkit.getPluginManager().getPlugin("BattleRoyale"));
                    }
                }
            }, 20, 20);
        }

        if (commandName.equals("stop")) {

            Bukkit.getScheduler().cancelTasks(Bukkit.getPluginManager().getPlugin("BattleRoyale"));

            for (World world : Bukkit.getWorlds()) {
                world.getWorldBorder().setSize(1000000, 0);
            }

            //Вырубить поток
            //Забрать Способности
        }
        return false;
    }
}
