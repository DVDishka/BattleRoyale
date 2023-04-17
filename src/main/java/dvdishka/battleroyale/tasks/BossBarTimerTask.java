package dvdishka.battleroyale.tasks;

import dvdishka.battleroyale.common.CommonVariables;
import dvdishka.battleroyale.classes.UpdateEvent;
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

            Bukkit.getGlobalRegionScheduler().runDelayed(CommonVariables.plugin, (task -> {

                Bukkit.getPluginManager().callEvent(new UpdateEvent());
            }), time);
        }

        for (int i = changePeriod; i <= time; i += changePeriod) {

            Bukkit.getGlobalRegionScheduler().runDelayed(CommonVariables.plugin, (task) -> {

                if (!CommonVariables.isGameStarted) {
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
