package dvdishka.battleroyale.handlers;

import dvdishka.battleroyale.common.CommonVariables;
import dvdishka.battleroyale.threads.PlayerUpdater;
import dvdishka.battleroyale.threads.Logic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldBorder;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

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
            Thread timer = new Logic(bossBar, "timer");
            Thread playerUpdater = new PlayerUpdater(bossBar, "playerUpdater");
            timer.start();
            playerUpdater.start();
            WorldBorder worldBorder = Bukkit.getWorld("world").getWorldBorder();
            worldBorder.setCenter(0, 0);
            worldBorder.setSize(100000);
            worldBorder.setSize(CommonVariables.zones.get(0), CommonVariables.times.get(0));
            CommonVariables.setZoneStage(1);
            BukkitScheduler scheduler = Bukkit.getScheduler();
            int task = scheduler.scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("BattleRoyale"), new Runnable() {
                @Override
                public void run() {
                    if (CommonVariables.getZoneStage() != CommonVariables.getExecutedZoneStage() && CommonVariables.getZoneStage() <= CommonVariables.zones.size()) {
                        CommonVariables.setExecutedZoneStage(CommonVariables.getZoneStage());
                        Bukkit.getWorld("world").getWorldBorder().setSize(CommonVariables.zones.get(
                                CommonVariables.getZoneStage() - 1), CommonVariables.times.get(CommonVariables.getZoneStage() - 1));
                    }
                    if (CommonVariables.getZoneStage() == CommonVariables.zones.size() + 1) {
                        Bukkit.getWorld("world").getWorldBorder().setSize(2, CommonVariables.finalZoneTime);
                        scheduler.cancelTasks(Bukkit.getPluginManager().getPlugin("BattleRoyale"));
                    }
                }
            }, 60, 60);
        }
        return false;
    }
}
