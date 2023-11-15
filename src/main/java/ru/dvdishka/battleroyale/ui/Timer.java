package ru.dvdishka.battleroyale.ui;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.logic.event.NextGameStageEvent;
import ru.dvdishka.battleroyale.logic.classes.ZonePhase;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.Scheduler;

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
            this.timer.setTitle("" + ChatColor.GREEN + ChatColor.BOLD + "BREAK" + ChatColor.RESET +
                    ChatColor.GREEN + ChatColor.BOLD + " | " + ChatColor.RESET +
                    getTimeText(zonePhase) +
                    ChatColor.GREEN + ChatColor.BOLD + " | " + ChatColor.RESET +
                    getPVPText() + ChatColor.RESET +
                    ChatColor.GREEN + ChatColor.BOLD + " | " + ChatColor.RESET +
                    getReviveText());

        } else if (zonePhase.equals(ZonePhase.ACTIVE)) {

            this.timer.setColor(BarColor.RED);
            this.timer.setTitle("" + ChatColor.RED + ChatColor.BOLD + "NARROWING" + ChatColor.RESET +
                    ChatColor.RED + ChatColor.BOLD + " | " + ChatColor.RESET +
                    getTimeText(zonePhase) +
                    ChatColor.RED + ChatColor.BOLD + " | " + ChatColor.RESET +
                    getPVPText() + ChatColor.RESET +
                    ChatColor.RED + ChatColor.BOLD + " | " + ChatColor.RESET +
                    getReviveText());

        } else {

            this.timer.setColor(BarColor.PINK);
            this.timer.setTitle("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "MOVING" + ChatColor.RESET +
                    ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " | " + ChatColor.RESET +
                    getTimeText(zonePhase) +
                    ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " | " + ChatColor.RESET +
                    getPVPText() + ChatColor.RESET +
                    ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " | " + ChatColor.RESET +
                    getReviveText());
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

    private String getReviveText() {

        String reviveText;

        if (Common.isRevivalEnabled) {
            reviveText = ChatColor.BOLD + "REVIVE: " + ChatColor.RESET + ChatColor.GREEN + ChatColor.BOLD + "YES";
        } else {
            reviveText = ChatColor.BOLD + "REVIVE: " + ChatColor.RESET + ChatColor.RED + ChatColor.BOLD + "NO";
        }

        return reviveText;
    }

    private String getTimeText(ZonePhase zonePhase) {

        String timeText;

        if (zonePhase.equals(ZonePhase.BREAK)) {

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
        } else if (zonePhase.equals(ZonePhase.BREAK)) {

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

        } else {

            timeText =
                    ChatColor.BOLD +
                            String.valueOf(Timer.getInstance().getTime() / 3600 / 10) +
                            String.valueOf(Timer.getInstance().getTime() / 3600 % 10) +
                            ChatColor.LIGHT_PURPLE + ChatColor.BOLD + ":" + ChatColor.RESET + ChatColor.BOLD +
                            String.valueOf(Timer.getInstance().getTime() / 60 % 60 / 10) +
                            String.valueOf(Timer.getInstance().getTime() / 60 % 10) +
                            ChatColor.LIGHT_PURPLE + ChatColor.BOLD + ":" + ChatColor.RESET + ChatColor.BOLD +
                            String.valueOf(Timer.getInstance().getTime() % 60 / 10) +
                            String.valueOf(Timer.getInstance().getTime() % 10) +
                            ChatColor.RESET;
        }

        return timeText;
    }
}
