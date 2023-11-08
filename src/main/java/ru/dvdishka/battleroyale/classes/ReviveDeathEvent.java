package ru.dvdishka.battleroyale.classes;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ReviveDeathEvent extends Event {

    private Player player;
    public static final HandlerList handlerList = new HandlerList();

    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public ReviveDeathEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }
}
