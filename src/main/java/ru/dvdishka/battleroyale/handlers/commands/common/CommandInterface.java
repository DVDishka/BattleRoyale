package ru.dvdishka.battleroyale.handlers.commands.common;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import ru.dvdishka.battleroyale.logic.Common;

public interface CommandInterface {

    void execute(CommandSender sender, CommandArguments args);

    default void returnSuccess(String message, CommandSender sender) {
        Common.returnSuccess(message, sender);
    }

    @SuppressWarnings("unused")
    default void returnSuccess(String message, CommandSender sender, TextColor color) {
        Common.returnSuccess(message, sender, color);
    }

    default void returnFailure(String message, CommandSender sender) {
        Common.returnFailure(message, sender);
    }

    @SuppressWarnings("unused")
    default void returnFailure(String message, CommandSender sender, TextColor color) {
        Common.returnFailure(message, sender, color);
    }

    default void returnWarning(String message, CommandSender sender, TextColor color) {
        Common.returnWarning(message, sender, color);
    }

    default void returnWarning(String message, CommandSender sender) {
        Common.returnWarning(message, sender);
    }

    default void sendMessage(String message, CommandSender sender) {
        Common.sendMessage(message, sender);
    }
}
