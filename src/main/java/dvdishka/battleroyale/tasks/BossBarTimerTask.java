package dvdishka.battleroyale.tasks;

import dvdishka.battleroyale.common.CommonVariables;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;

import java.util.concurrent.TimeUnit;

public class BossBarTimerTask implements Runnable {

    private final BossBar bossBar;
    private final int time;
    private String barInfo;
    private BarColor barColor;

    public BossBarTimerTask(BossBar bossBar, int time, String barInfo, BarColor barColor) {

        this.bossBar = bossBar;
        this.time = time;
        this.barInfo = barInfo;
        this.barColor = barColor;
    }

    @Override
    public void run() {

        bossBar.setProgress(0.0);
        bossBar.setTitle(barInfo);
        bossBar.setColor(barColor);

        int changePeriod = time * 60 / 10;
        for (int i = changePeriod; i <= time * 60; i += changePeriod) {

            Bukkit.getAsyncScheduler().runDelayed(CommonVariables.plugin, (task) -> {

                if (bossBar.getProgress() + 0.1 <= 1) {
                    bossBar.setProgress(bossBar.getProgress() + 0.1);
                } else {
                    bossBar.setProgress(1);
                }
            }, i, TimeUnit.SECONDS);
        }
    }
}
