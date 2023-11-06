package ru.dvdishka.battleroyale.handlers;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.classes.NextGameStageEvent;
import ru.dvdishka.battleroyale.classes.ZonePhase;
import ru.dvdishka.battleroyale.common.Common;
import ru.dvdishka.battleroyale.common.Scheduler;

public class Timer {

    private static Timer instance = null;

    private BossBar timer;
    private int timeSeconds = 0;
    private ZonePhase zonePhase;

    ScheduledTask lastSecondsTimerTask = null;

    private Timer() {}

    public static Timer getInstance() {
        if (instance == null) {
            instance = new Timer();
        }
        return instance;
    }

    public void startTimer(int timeSeconds, ZonePhase zonePhase, boolean callEvent) {

        if (lastSecondsTimerTask != null) {
            lastSecondsTimerTask.cancel();
        }

        this.timeSeconds = timeSeconds;
        this.zonePhase = zonePhase;

        this.timer.setProgress(0.0);

        int time = timeSeconds * 20;

        int changePeriod = time / 10;

        if (callEvent) {

            Scheduler.getScheduler().runSyncDelayed(Common.plugin, (scheduledTask) -> {

                Bukkit.getPluginManager().callEvent(new NextGameStageEvent());
            }, time);
        }

        lastSecondsTimerTask = Scheduler.getScheduler().runSyncRepeatingTask(Common.plugin, (scheduledTask) -> {

            this.timeSeconds--;

            updateTimerTitle();

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

        updateTimerTitle();
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

    private void updateTimerTitle() {

        if (zonePhase.equals(ZonePhase.BREAK)) {

            this.timer.setColor(BarColor.GREEN);
            this.timer.setTitle(getTimeText(true) + ChatColor.GREEN + ChatColor.BOLD + " | " + ChatColor.RESET + ChatColor.GREEN + ChatColor.BOLD + "BREAK" + ChatColor.RESET + ChatColor.GREEN + ChatColor.BOLD + " | " + ChatColor.RESET + getPVPText());

        } else {

            this.timer.setColor(BarColor.RED);
            this.timer.setTitle(getTimeText(false) + ChatColor.RED + ChatColor.BOLD + " | " + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + "NARROWING" + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + " | " + ChatColor.RESET + getPVPText());
        }
    }

    private String getPVPText() {

        String pvpText;

        if (!Bukkit.getWorld("world").getPVP())  {
            pvpText = ChatColor.BOLD + "PVP: " + ChatColor.RESET + ChatColor.GREEN + ChatColor.BOLD + "NO";
        } else {
            pvpText = ChatColor.BOLD + "PVP: " + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + "YES";
        }

        return pvpText;
    }

    private String getTimeText(boolean isBreak) {

        String timeText;

        if (isBreak) {
            timeText =
                    ChatColor.BOLD +
                            String.valueOf(Timer.getInstance().getTime() / 3600 / 10) +
                            String.valueOf(Timer.getInstance().getTime() / 3600 % 10) +
                            ChatColor.GREEN + ChatColor.BOLD + ":" + ChatColor.RESET + ChatColor.BOLD +
                            String.valueOf(Timer.getInstance().getTime() / 60 % 60 / 10) +
                            String.valueOf(Timer.getInstance().getTime() / 60 % 10) +
                            ChatColor.GREEN + ChatColor.BOLD + ":" + ChatColor.RESET + ChatColor.BOLD +
                            String.valueOf(Timer.getInstance().getTime() % 60 / 10) +
                            String.valueOf(Timer.getInstance().getTime() % 10) +
                            ChatColor.RESET;
        } else {
            timeText =
                    ChatColor.BOLD +
                            String.valueOf(Timer.getInstance().getTime() / 3600 / 10) +
                            String.valueOf(Timer.getInstance().getTime() / 3600 % 10) +
                            ChatColor.RED + ChatColor.BOLD + ":" + ChatColor.RESET + ChatColor.BOLD +
                            String.valueOf(Timer.getInstance().getTime() / 60 % 60 / 10) +
                            String.valueOf(Timer.getInstance().getTime() / 60 % 10) +
                            ChatColor.RED + ChatColor.BOLD + ":" + ChatColor.RESET + ChatColor.BOLD +
                            String.valueOf(Timer.getInstance().getTime() % 60 / 10) +
                            String.valueOf(Timer.getInstance().getTime() % 10) +
                            ChatColor.RESET;
        }
        return timeText;
    }
}
