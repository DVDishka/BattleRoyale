package ru.dvdishka.battleroyale.handlers;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.classes.NextGameStageEvent;
import ru.dvdishka.battleroyale.common.Common;
import ru.dvdishka.battleroyale.common.Scheduler;

public class Timer {

    private static Timer instance = null;

    private BossBar timer;
    private int timeSeconds = 0;

    ScheduledTask lastSecondsTimerTask = null;

    private Timer() {}

    public static Timer getInstance() {
        if (instance == null) {
            instance = new Timer();
        }
        return instance;
    }

    public void startTimer(int timeSeconds, String barInfo, BarColor barColor, boolean callEvent) {

        if (lastSecondsTimerTask != null) {
            lastSecondsTimerTask.cancel();
        }

        this.timeSeconds = timeSeconds;

        this.timer.setProgress(0.0);
        this.timer.setTitle(barInfo);
        this.timer.setColor(barColor);

        int time = timeSeconds * 20;

        int changePeriod = time / 10;

        if (callEvent) {

            Scheduler.getScheduler().runSyncDelayed(Common.plugin, (scheduledTask) -> {

                Bukkit.getPluginManager().callEvent(new NextGameStageEvent());
            }, time);
        }

        lastSecondsTimerTask = Scheduler.getScheduler().runSyncRepeatingTask(Common.plugin, (scheduledTask) -> {
            this.timeSeconds--;
            if (this.timeSeconds <= 0) {
                scheduledTask.cancel();
            }}, 20, 20);

        for (int i = changePeriod; i <= time; i += changePeriod) {

            Scheduler.getScheduler().runSyncDelayed(Common.plugin, (scheduledTask) -> {

                if (!Common.isGameStarted) {
                    return;
                }

                if (this.timer.getProgress() + 0.1 <= 1) {
                    this.timer.setProgress(this.timer.getProgress() + 0.1);
                } else {
                    this.timer.setProgress(1);
                }
            }, i);
        }
    }

    public int getTime() {
        return timeSeconds;
    }

    public void addViewer(Player player) {
        this.timer.addPlayer(player);
    }

    public void register() {
        this.timer = Bukkit.createBossBar("", BarColor.RED, BarStyle.SEGMENTED_10);
        this.timer.setVisible(true);
    }

    public void unregister() {
        try {
            this.timer.removeAll();
            this.timer.setVisible(false);
        } catch (Exception ignored) {}
    }
}
