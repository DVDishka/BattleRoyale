package ru.dvdishka.battleroyale.logic.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class NextGameStageEvent extends Event {

    public static final HandlerList handlerList = new HandlerList();

    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public NextGameStageEvent() {}
}
