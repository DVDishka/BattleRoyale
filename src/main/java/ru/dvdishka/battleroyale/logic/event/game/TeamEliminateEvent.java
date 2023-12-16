package ru.dvdishka.battleroyale.logic.event.game;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TeamEliminateEvent extends Event {

    private final String teamName;
    private final TextColor teamColor;

    public static final HandlerList handlerList = new HandlerList();

    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public TeamEliminateEvent(String teamName, TextColor teamColor) {
        this.teamName = teamName;
        this.teamColor = teamColor;
    }

    public String getTeamName() {
        return this.teamName;
    }

    public TextColor getTeamColor() {
        return this.teamColor;
    }
}
