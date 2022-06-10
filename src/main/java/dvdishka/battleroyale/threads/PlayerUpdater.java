package dvdishka.battleroyale.threads;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class PlayerUpdater extends Thread {

    private BossBar bossBar;

    public PlayerUpdater(BossBar bossBar, String name) {
        super(name);
        this.bossBar = bossBar;
    }

    @Override
    public void run() {

        while (true) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                boolean flag = false;
                for (Player player : bossBar.getPlayers()) {
                    if (player == onlinePlayer) {
                        flag = true;
                    }
                }
                if (!flag) {
                    this.bossBar.addPlayer(onlinePlayer);
                }
            }
            try {
                sleep(1000);
            } catch (Exception ignored) {

            }
        }
    }
}
