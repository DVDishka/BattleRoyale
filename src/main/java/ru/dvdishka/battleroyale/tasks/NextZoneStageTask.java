package ru.dvdishka.battleroyale.tasks;

import ru.dvdishka.battleroyale.common.Common;
import ru.dvdishka.battleroyale.common.ConfigVariables;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.concurrent.TimeUnit;

public class NextZoneStageTask implements Runnable {


    @Override
    public void run() {

        for (World world : Bukkit.getWorlds()) {

            world.getWorldBorder().setSize(ConfigVariables.zones.get(Common.zoneStage),
                    TimeUnit.SECONDS, ConfigVariables.times.get(Common.zoneStage));
        }
    }
}
