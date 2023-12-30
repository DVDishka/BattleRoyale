package ru.dvdishka.battleroyale.handlers.commands.common;

import dev.jorel.commandapi.CommandPermission;

public enum Permission {

    ADMIN(CommandPermission.fromString("battleroyale")),
    START_STOP(CommandPermission.fromString("battleroyale.start_stop")),
    START_BOX(CommandPermission.fromString("battleroyale.start_box")),
    PLAYER_EDIT(CommandPermission.fromString("battleroyale.player_edit")),
    DROP_EDIT(CommandPermission.fromString("battleroyale.drop_edit")),;

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
