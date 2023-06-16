package dvdishka.battleroyale.common;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public class Scheduler {

    public static Scheduler getScheduler() {
        return new Scheduler();
    }

    public void runSync(Plugin plugin, Runnable task) {
        if (CommonVariables.isFolia) {
            Bukkit.getGlobalRegionScheduler().run(plugin, (scheduledTask) -> task.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public void runSyncDelayed(Plugin plugin, Runnable task, long delayTicks) {
        if (CommonVariables.isFolia) {
            Bukkit.getGlobalRegionScheduler().runDelayed(plugin, (scheduledTask) -> task.run(), delayTicks);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    public void runSyncRepeatingTask(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        if (CommonVariables.isFolia) {
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, (scheduledTask) -> task.run(), delayTicks, periodTicks);
        } else {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, task, delayTicks, periodTicks);
        }
    }

    public void runAsync(Plugin plugin, Runnable task) {
        if (CommonVariables.isFolia) {
            Bukkit.getAsyncScheduler().runNow(plugin, (scheduledTask) -> task.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    public void runAsyncDelayed(Plugin plugin, Runnable task, long delayTicks) {
        if (CommonVariables.isFolia) {
            Bukkit.getAsyncScheduler().runDelayed(plugin, (scheduledTask) -> task.run(), delayTicks * 20, TimeUnit.SECONDS);
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delayTicks);
        }
    }

    public void runAsyncRepeatingTask(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        if (CommonVariables.isFolia) {
            Bukkit.getAsyncScheduler().runAtFixedRate(plugin, (scheduledTask) -> task.run(), delayTicks * 20, periodTicks * 20, TimeUnit.SECONDS);
        } else {
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, task, delayTicks, periodTicks);
        }
    }

    public void runPlayerTask(Plugin plugin, Player player, Runnable task) {
        if (CommonVariables.isFolia) {
            player.getScheduler().run(plugin, (scheduledTask) -> task.run(), null);
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public void runRegionTask(Plugin plugin, Location location, Runnable task) {
        if (CommonVariables.isFolia) {
            Bukkit.getRegionScheduler().run(plugin, location, (scheduledTask) -> task.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public static void cancelTasks(Plugin plugin) {
        if (CommonVariables.isFolia) {
            Bukkit.getAsyncScheduler().cancelTasks(plugin);
            Bukkit.getGlobalRegionScheduler().cancelTasks(plugin);
        } else {
            Bukkit.getScheduler().cancelTasks(plugin);
        }
    }
}
