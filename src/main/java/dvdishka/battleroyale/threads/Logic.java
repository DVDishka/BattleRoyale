package dvdishka.battleroyale.threads;

import com.destroystokyo.paper.Title;
import dvdishka.battleroyale.common.CommonVariables;
import org.bukkit.*;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.security.spec.ECField;
import java.util.HashSet;
import java.util.Random;

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
            if (i != 0) {
                Title.Builder titleBulilder = (Title.builder().title(ChatColor.RED + "New Zone"));
                if (i == 1) {
                    titleBulilder.subtitle(ChatColor.GOLD + "PVP enabled!");
                }
                Title title = titleBulilder.build();
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.sendTitle(title);
                }
            } else {

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
                if (time / 10 != 0) {
                    if (counter % (time / 10) == 0) {
                        stage -= 0.1;
                        if (stage >= 0) {
                            bossBar.setProgress(stage);
                        } else {
                            bossBar.setProgress(0);
                        }
                    }
                }
                try {
                    sleep(1000);
                } catch (Exception e) {
                    CommonVariables.logger.warning("Something went wrong in Logic Thread!");
                }
            }

            if (i < CommonVariables.zones.size() - 1) {
                bossBar.setTitle("Next zone " + -1 * (CommonVariables.zones.get(i + 1) / 2) + " to " +
                        (CommonVariables.zones.get(i + 1)) / 2);
            } else {
                Random random = new Random();
                int zoneCenter = random.nextInt(-1 * (CommonVariables.zones.get(CommonVariables.zones.size() - 1)) / 2,
                        CommonVariables.zones.get(CommonVariables.zones.size() - 1) / 2);
                CommonVariables.setFinalZoneCenter(zoneCenter);
                if (zoneCenter <= 0) {
                    bossBar.setTitle("Next zone " + zoneCenter + " to " + (zoneCenter + 1));
                } else {
                    bossBar.setTitle("Next zone " + zoneCenter + " to " + (zoneCenter - 1));
                }
            }
            bossBar.setProgress(1);
            double breakStage = 1;
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendTitle(Title.builder().title(ChatColor.GOLD + "Break " + CommonVariables.timeOut
                        + " Seconds").build());
            }
            try {
                sleep(1000);
            } catch (Exception e) {
                CommonVariables.logger.warning("Something went wrong in Logic Thread!");
            }

            counter = CommonVariables.timeOut;
            try {
                while (counter > 0) {
                    counter--;
                    try {
                        if (counter % (CommonVariables.timeOut / 10) == 0) {
                            breakStage -= 0.1;
                            bossBar.setProgress(breakStage);
                        }
                    } catch (Exception ignored) {}
                    sleep(1000);
                }
            } catch (Exception e) {
                CommonVariables.logger.warning("Something went wrong in Logic Thread!");
            }
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendTitle(Title.builder().title(ChatColor.RED + "Final zone").build());
        }
        if (CommonVariables.getFinalZoneCenter() <= 0) {
            bossBar.setTitle("Final Zone " + CommonVariables.getFinalZoneCenter() + " to " +
                    (CommonVariables.getFinalZoneCenter() + 1));
        } else {
            bossBar.setTitle("Final Zone " + CommonVariables.getFinalZoneCenter() + " to " +
                    (CommonVariables.getFinalZoneCenter() - 1));
        }
        CommonVariables.setZoneStage(CommonVariables.zones.size() + 1);
        bossBar.setProgress(1);
        int counter = CommonVariables.finalZoneTime;
        double stage = 1;
        while (counter > 0) {
            counter--;
            try {
                if (counter % (CommonVariables.finalZoneTime / 10) == 0) {
                    stage -= 0.1;
                    bossBar.setProgress(stage);
                }
            } catch (Exception ignored) {}
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
                if (onlinePlayer.getGameMode() == GameMode.SURVIVAL) {
                    liveTeamsHashSet.add(Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(Bukkit.getOfflinePlayer(onlinePlayer.getUniqueId())).getName());
                    winner = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(Bukkit.getOfflinePlayer(onlinePlayer.getUniqueId())).getName();
                }
            }
            if (liveTeamsHashSet.size() == 1) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.sendTitle(Title.builder().title(ChatColor.GOLD + winner + " " + "Won!").build());
                }
                return;
            }
            try {
                sleep(500);
            } catch (Exception e) {
                CommonVariables.logger.warning("Something went wrong while trying to sleep in Logic Thread!");
            }
        }
    }
}
