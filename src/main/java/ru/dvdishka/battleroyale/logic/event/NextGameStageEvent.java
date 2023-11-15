package ru.dvdishka.battleroyale.logic.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NextGameStageEvent extends Event {

    public static final HandlerList handlerList = new HandlerList();

    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public NextGameStageEvent() {}
}
