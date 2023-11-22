package ru.dvdishka.battleroyale.handlers.commands.drop;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.classes.drop.DropContainer;

public class DropListCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        Component message = Component.empty();

        message = message
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

            message = message
                    .append(Component.text("-".repeat(30))
                            .color(NamedTextColor.AQUA))
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
                    .append(followButton)
                    .append(Component.newline());
        }

        Component unFollowButton = Component.empty();

        unFollowButton = unFollowButton
                .append(Component.text("[UNFOLLOW]")
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD)
                        .clickEvent(ClickEvent.runCommand("/battleroyale drop unFollow")));

        message = message
                .append(unFollowButton)
                .append(Component.newline());

        message = message
                .append(Component.text("-".repeat(26))
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD));

        sender.sendMessage(message);
    }
}