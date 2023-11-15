package ru.dvdishka.battleroyale.handlers.commands.common;

import dev.jorel.commandapi.CommandPermission;

public enum Permission {

    START_STOP(CommandPermission.fromString("battleroyale.start_stop")),
    START_BOX(CommandPermission.fromString("battleroyale.start_box")),
    TEAM_CREATE(CommandPermission.fromString("battleroyale.team.create")),
    REVIVE(CommandPermission.fromString("battleroyale.revive")),
    DROP_CREATE(CommandPermission.fromString("battleroyale.drop_create"));

    private final CommandPermission commandPermission;

    Permission(CommandPermission commandPermission) {
        this.commandPermission = commandPermission;
    }

    public CommandPermission getPermission() {
        return commandPermission;
    }
}
