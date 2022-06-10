package dvdishka.battleroyale.common;

import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class CommonVariables {

    public static final Logger logger = Bukkit.getLogger();

    public static final List<Integer> zones = Arrays.asList(2000, 1000, 700, 400, 200);

    public static final int finalZoneTime = 60;

    private static volatile int zoneStage = 0;

    public synchronized static int getZoneStage() {
        return zoneStage;
    }

    public synchronized static void setZoneStage(int num) {
        zoneStage = num;
    }

    private static volatile int executedZoneStage = 0;

    public synchronized static int getExecutedZoneStage() {
        return executedZoneStage;
    }

    public synchronized static void setExecutedZoneStage(int num) {
        executedZoneStage = num;
    }

    public static final List<Integer> times = Arrays.asList(30, 30, 30, 30, 30);
}
