package ru.dvdishka.battleroyale.tasks;

import ru.dvdishka.battleroyale.common.Common;
import ru.dvdishka.battleroyale.classes.NextGameStageEvent;
import ru.dvdishka.battleroyale.common.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;

public class BossBarTimerTask implements Runnable {

    private final BossBar bossBar;
    private final int time;
    private String barInfo;
    private BarColor barColor;
    private boolean callEvent;

    public BossBarTimerTask(BossBar bossBar, int time, String barInfo, BarColor barColor, boolean callEvent) {

        this.bossBar = bossBar;
        this.time = time * 20;
        this.barInfo = barInfo;
        this.barColor = barColor;
        this.callEvent = callEvent;
    }

    @Override
    public void run() {

        bossBar.setProgress(0.0);
        bossBar.setTitle(barInfo);
        bossBar.setColor(barColor);

        int changePeriod = time / 10;

        if (callEvent) {

            Scheduler.getScheduler().runSyncDelayed(Common.plugin, () -> {

                Bukkit.getPluginManager().callEvent(new NextGameStageEvent());
            }, time);
        }

        for (int i = changePeriod; i <= time; i += changePeriod) {

            Scheduler.getScheduler().runSyncDelayed(Common.plugin, () -> {

                if (!Common.isGameStarted) {
                    return;
                }

                if (bossBar.getProgress() + 0.1 <= 1) {
                    bossBar.setProgress(bossBar.getProgress() + 0.1);
                } else {
                    bossBar.setProgress(1);
                }
            }, i);
        }
    }
}
