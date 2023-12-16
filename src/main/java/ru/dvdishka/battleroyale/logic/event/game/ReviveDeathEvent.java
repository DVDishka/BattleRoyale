package ru.dvdishka.battleroyale.logic.event.game;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ReviveDeathEvent extends Event {

    private final Player player;
    public static final HandlerList handlerList = new HandlerList();

    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    @SuppressWarnings("unused")
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
