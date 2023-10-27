package ru.dvdishka.battleroyale.common;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Scheduler {

    public static Scheduler getScheduler() {
        return new Scheduler();
    }

    public ScheduledTask runSync(Plugin plugin, Consumer<ScheduledTask> task) {
        return Bukkit.getGlobalRegionScheduler().run(plugin, task);
    }

    public ScheduledTask runSyncDelayed(Plugin plugin, Consumer<ScheduledTask> task, long delayTicks) {
        return Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task, delayTicks);
    }

    public ScheduledTask runSyncRepeatingTask(Plugin plugin, Consumer<ScheduledTask> task, long delayTicks, long periodTicks) {
        return Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, task, delayTicks, periodTicks);
    }

    public ScheduledTask runAsync(Plugin plugin, Consumer<ScheduledTask> task) {
        return Bukkit.getAsyncScheduler().runNow(plugin, task);
    }

    public ScheduledTask runAsyncDelayed(Plugin plugin, Consumer<ScheduledTask> task, long delayTicks) {
        return Bukkit.getAsyncScheduler().runDelayed(plugin, task, delayTicks * 20, TimeUnit.SECONDS);
    }

    public ScheduledTask runAsyncRepeatingTask(Plugin plugin, Consumer<ScheduledTask> task, long delayTicks, long periodTicks) {
        return Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task, delayTicks / 20, periodTicks / 20, TimeUnit.SECONDS);
    }

    public ScheduledTask runPlayerTask(Plugin plugin, Player player, Consumer<ScheduledTask> task) {
        return player.getScheduler().run(plugin, task, null);
    }

    public ScheduledTask runRegionTask(Plugin plugin, Location location, Consumer<ScheduledTask> task) {
        return Bukkit.getRegionScheduler().run(plugin, location, task);
    }

    public static void cancelTasks(Plugin plugin) {
        Bukkit.getAsyncScheduler().cancelTasks(plugin);
        Bukkit.getGlobalRegionScheduler().cancelTasks(plugin);
    }
}
