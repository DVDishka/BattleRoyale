package ru.dvdishka.battleroyale.handlers.commands.common;

import dev.jorel.commandapi.CommandPermission;

public enum Permission {

    START_STOP(CommandPermission.fromString("battleroyale.start_stop")),
    START_BOX(CommandPermission.fromString("battleroyale.start_box")),
    REVIVE_KILL(CommandPermission.fromString("battleroyale.revive_kill")),
    DROP(CommandPermission.fromString("battleroyale.drop")),;

    private final CommandPermission commandPermission;

    Permission(CommandPermission commandPermission) {
        this.commandPermission = commandPermission;
    }

    public CommandPermission getPermission() {
        return commandPermission;
    }

    public String getStringPermission() {
        return commandPermission.toString();
    }
}
