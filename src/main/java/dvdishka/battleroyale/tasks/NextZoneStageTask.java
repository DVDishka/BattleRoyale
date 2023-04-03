package dvdishka.battleroyale.tasks;

import dvdishka.battleroyale.common.CommonVariables;
import dvdishka.battleroyale.common.ConfigVariables;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.concurrent.TimeUnit;

public class NextZoneStageTask implements Runnable {


    @Override
    public void run() {

        for (World world : Bukkit.getWorlds()) {

            world.getWorldBorder().setSize(ConfigVariables.zones.get(CommonVariables.zoneStage),
                    TimeUnit.MINUTES, ConfigVariables.times.get(CommonVariables.zoneStage));
        }
    }
}
