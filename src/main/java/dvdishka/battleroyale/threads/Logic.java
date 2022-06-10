package dvdishka.battleroyale.threads;

import com.destroystokyo.paper.Title;
import dvdishka.battleroyale.common.CommonVariables;
import org.bukkit.*;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class Logic extends Thread {

    private BossBar bossBar;

    public Logic(BossBar bossBar, String name) {
        super(name);
        this.bossBar = bossBar;
    }
    @Override
    public void run() {
        for (int i = 0; i < CommonVariables.zones.size(); i++) {
            Integer zone = CommonVariables.zones.get(i);
            Integer time = CommonVariables.times.get(i);
            CommonVariables.setZoneStage(i + 1);
            bossBar.setProgress(1);
            Title.Builder titleBulilder = (Title.builder().title(ChatColor.RED + "New Zone"));
            if (i == 0) {
                titleBulilder.subtitle(ChatColor.GOLD + "PVP enabled!");
            }
            Title title = titleBulilder.build();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendTitle(title);
            }
            if (i == 0) {
                bossBar.setTitle("Safe zone " + -1 * (zone / 2) + " to " + (zone / 2));
            } else {
                bossBar.setTitle("Safe zone " + -1 * (zone / 2) + " to " + (zone / 2) +
                        " Danger zone " + -1 * (CommonVariables.zones.get(i - 1) / 2) + " to " +
                        (CommonVariables.zones.get(i - 1) / 2));
            }
            int counter = time;
            double stage = 1;
            while (counter > 0) {
                counter--;
                if (counter % (time / 10) == 0) {
                    stage -= 0.1;
                    if (stage >= 0) {
                        bossBar.setProgress(stage);
                    } else {
                        bossBar.setProgress(0);
                    }
                }
                try {
                    sleep(1000);
                } catch (Exception e) {
                    CommonVariables.logger.warning("Something went wrong while trying to sleep in Logic Thread!");
                }
            }
        }
        CommonVariables.setZoneStage(CommonVariables.zones.size() + 1);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendTitle(Title.builder().title(ChatColor.RED + "Final zone").build());
        }
        bossBar.setTitle("Final Zone");
        bossBar.setProgress(1);
        int counter = CommonVariables.finalZoneTime;
        double stage = 1;
        while (counter > 0) {
            counter--;
            if (counter % (CommonVariables.finalZoneTime / 10) == 0) {
                stage -= 0.1;
                bossBar.setProgress(stage);
            }
            try {
                sleep(1000);
            } catch (Exception e) {
                CommonVariables.logger.warning("Something went wrong while trying to sleep in Logic Thread!");
            }
        }
        while (true) {
            HashSet<String> liveTeamsHashSet = new HashSet<>();
            String winner = "";
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getGameMode() == GameMode.ADVENTURE) {
                    liveTeamsHashSet.add(Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(Bukkit.getOfflinePlayer(onlinePlayer.getUniqueId())).getName());
                    winner = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(Bukkit.getOfflinePlayer(onlinePlayer.getUniqueId())).getName();
                }
            }
            if (liveTeamsHashSet.size() == 1) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.sendTitle(Title.builder().title(ChatColor.GOLD + winner + " " + "Won!").build());
                    return;
                }
            }
            try {
                sleep(500);
            } catch (Exception e) {
                CommonVariables.logger.warning("Something went wrong while trying to sleep in Logic Thread!");
            }
        }
    }
}
