package dvdishka.battleroyale.tasks.endless;

import dvdishka.battleroyale.classes.Team;
import dvdishka.battleroyale.common.CommonVariables;
import dvdishka.battleroyale.common.Scheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeamActionBarTask implements Runnable {

    @Override
    public void run() {

        for (Team team : Team.teams) {
            for (String playerName : team.getPlayers()) {

                Player player = Bukkit.getPlayer(playerName);
                if (player != null) {

                    Scheduler.getScheduler().runPlayerTask(CommonVariables.plugin, player, () -> {
                        player.sendActionBar(Component.text(team.getName()).color(team.getColor()));
                    });
                }
            }
        }
    }
}
