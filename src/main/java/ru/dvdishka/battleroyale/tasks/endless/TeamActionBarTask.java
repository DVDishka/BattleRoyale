package ru.dvdishka.battleroyale.tasks.endless;

import ru.dvdishka.battleroyale.classes.Team;
import ru.dvdishka.battleroyale.common.Common;
import ru.dvdishka.battleroyale.common.Scheduler;
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

                    Scheduler.getScheduler().runPlayerTask(Common.plugin, player, () -> {
                        player.sendActionBar(Component.text(team.getName()).color(team.getColor()));
                    });
                }
            }
        }
    }
}
