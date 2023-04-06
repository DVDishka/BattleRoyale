package dvdishka.battleroyale.tasks;

import dvdishka.battleroyale.common.CommonVariables;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class ZoneMovingTask implements Runnable {

    private int xMove;
    private int zMove;
    private int duration;
    private int steps;

    public ZoneMovingTask(int xMove, int zMove, int duration, int steps) {

        this.xMove = xMove;
        this.zMove = zMove;
        this.duration = duration;
        this.steps = steps;
    }

    @Override
    public void run() {

        int step = duration * 20 / steps;

        for (int i = step; i <= duration * 1200; i += step) {

            Bukkit.getGlobalRegionScheduler().runDelayed(CommonVariables.plugin, (task) -> {
                for (World world : Bukkit.getWorlds()) {

                    int x = 0, z = 0;
                    if (xMove != 0) {
                        x = xMove;
                    }
                    if (zMove != 0) {
                        z = zMove;
                    }

                    world.getWorldBorder().setCenter(world.getWorldBorder().getCenter().x() + x,
                            world.getWorldBorder().getCenter().z() + z);
                }
            }, i);
        }
    }
}
