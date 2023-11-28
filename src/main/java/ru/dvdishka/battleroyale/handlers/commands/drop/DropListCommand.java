package ru.dvdishka.battleroyale.handlers.commands.drop;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.handlers.commands.common.Permission;
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainer;

public class DropListCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        if (DropContainer.getContainerList().isEmpty()){
            returnFailure("There are no drop containers yet", sender);
            return;
        }

        Component message = Component.empty();

        message = message
                .append(Component.newline())
                .append(Component.text("-".repeat(26))
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD))
                .append(Component.newline());

        message = message
                .append(Component.text("DROP LIST:")
                        .decorate(TextDecoration.BOLD))
                .append(Component.newline());

        for (DropContainer dropContainer : DropContainer.getContainerList()) {

            String worldName = dropContainer.getLocation().getWorld().getName();
            TextColor worldNameColor = NamedTextColor.WHITE;

            if (worldName.equals("world")) {
                worldName = "overworld";
                worldNameColor = NamedTextColor.DARK_GREEN;
            }

            if (worldName.equals("the_nether")) {
                worldName = "nether";
                worldNameColor = NamedTextColor.DARK_RED;
            }

            if (worldName.equals("the_end")) {
                worldName = "end";
                worldNameColor = NamedTextColor.DARK_PURPLE;
            }

            worldName = worldName.toUpperCase();

            Component followButton = Component.empty();

            followButton = followButton
                    .append(Component.text("[FOLLOW]")
                            .color(NamedTextColor.GREEN)
                            .decorate(TextDecoration.BOLD)
                            .clickEvent(ClickEvent.runCommand("/battleroyale drop follow " + "\"" + dropContainer.getName() + "\"")));

            Component deleteButton = Component.empty();

            deleteButton = deleteButton
                    .append(Component.text("[DELETE]")
                            .color(NamedTextColor.RED)
                            .decorate(TextDecoration.BOLD)
                            .clickEvent(ClickEvent.runCommand("/battleroyale drop delete " + "\"" + dropContainer.getName() + "\"")));

            message = message
                    .append(Component.text("=".repeat(24))
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD))
                    .append(Component.newline())
                    .append(Component.text(worldName)
                            .color(worldNameColor))
                    .append(Component.space())
                    .append(Component.text("X:"))
                    .append(Component.space())
                    .append(Component.text(dropContainer.getLocation().getBlockX()))
                    .append(Component.space())
                    .append(Component.text("Y:"))
                    .append(Component.space())
                    .append(Component.text(dropContainer.getLocation().getBlockY()))
                    .append(Component.space())
                    .append(Component.text("Z:"))
                    .append(Component.space())
                    .append(Component.text(dropContainer.getLocation().getBlockZ()))
                    .append(Component.newline())
                    .append(Component.text("-".repeat(25))
                            .color(NamedTextColor.YELLOW))
                    .append(Component.newline())
                    .append(followButton);

            if (sender.hasPermission(Permission.DROP.getStringPermission())) {
                message = message
                        .append(Component.space())
                        .append(deleteButton);
            }

            message = message
                    .append(Component.newline());
        }

        Component unFollowButton = Component.empty();

        unFollowButton = unFollowButton
                .append(Component.text("[UNFOLLOW]")
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD)
                        .clickEvent(ClickEvent.runCommand("/battleroyale drop unfollow")));

        message = message
                .append(Component.text("=".repeat(24))
                        .color(NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD))
                .append(Component.newline())
                .append(unFollowButton)
                .append(Component.newline());

        message = message
                .append(Component.text("-".repeat(26))
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD))
                .append(Component.newline());

        sender.sendMessage(message);
    }
}
