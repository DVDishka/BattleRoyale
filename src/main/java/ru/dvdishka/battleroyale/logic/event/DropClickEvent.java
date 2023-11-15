package ru.dvdishka.battleroyale.logic.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainer;

public class DropClickEvent extends Event {

    private final Player player;
    private final DropContainer dropContainer;

    private static final HandlerList handlerList = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public DropClickEvent(DropContainer dropContainer, Player player) {
        this.dropContainer = dropContainer;
        this.player = player;
    }

    public DropContainer getDropContainer() {
        return this.dropContainer;
    }

    public Player getPlayer() {
        return player;
    }
}
