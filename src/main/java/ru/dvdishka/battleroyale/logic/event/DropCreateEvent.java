package ru.dvdishka.battleroyale.logic.event;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.dvdishka.battleroyale.logic.classes.drop.DropType;

public class DropCreateEvent extends Event {

    private final DropType dropType;
    private final World world;

    private static final HandlerList handlerList = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public DropCreateEvent(DropType dropType, World world) {
        this.dropType = dropType;
        this.world = world;
    }

    public DropType getDropType() {
        return this.dropType;
    }

    public World getWorld() {
        return this.world;
    }
}
