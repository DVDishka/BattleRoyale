package dvdishka.battleroyale.tasks;

import dvdishka.battleroyale.classes.Team;
import dvdishka.battleroyale.common.CommonVariables;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
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

                    EntityScheduler playerScheduler = player.getScheduler();

                    playerScheduler.run(CommonVariables.plugin, (task) -> {
                        player.sendActionBar(Component.text(team.getName()).color(team.getColor()));
                    }, null);
                }
            }
        }
    }
}
