package ru.dvdishka.battleroyale.handlers;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import ru.dvdishka.battleroyale.logic.common.GameVariables;
import ru.dvdishka.battleroyale.logic.event.game.GameDeathEvent;
import ru.dvdishka.battleroyale.logic.event.game.ReviveDeathEvent;

public class EventHandler implements Listener {

    @org.bukkit.event.EventHandler
    public void onPortal(PlayerPortalEvent event) {

        if (GameVariables.isPortalLocked) {

            event.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onDeath(PlayerDeathEvent event) {

        if (GameVariables.isGameStarted) {

            if (GameVariables.isRevivalEnabled) {

                Bukkit.getPluginManager().callEvent(new ReviveDeathEvent(event.getPlayer()));
            }
            if (!GameVariables.isRevivalEnabled) {

                Bukkit.getPluginManager().callEvent(new GameDeathEvent(event.getPlayer().getName()));
            }
        }
    }
}
