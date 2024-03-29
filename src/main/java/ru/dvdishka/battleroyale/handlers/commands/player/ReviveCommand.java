package ru.dvdishka.battleroyale.handlers.commands.player;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.handlers.StartElytraHandler;
import ru.dvdishka.battleroyale.logic.Team;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.common.Common;
import ru.dvdishka.battleroyale.logic.common.GameVariables;
import ru.dvdishka.battleroyale.logic.common.PlayerVariables;
import ru.dvdishka.battleroyale.logic.common.PluginVariables;

public class ReviveCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        if (!GameVariables.isGameStarted) {
            returnFailure("You can not revive player while the game is not started", sender);
            return;
        }

        String revivePlayerName = (String) args.get("player");
        Player revivePlayer = Bukkit.getPlayer(revivePlayerName);

        if (!PlayerVariables.isDead(revivePlayerName)) {
            returnFailure("This player is not dead", sender);
            return;
        }

        PlayerVariables.removeDead(revivePlayerName);

        for (Player player : PlayerVariables.getOnlinePlayers()) {

            Common.sendNotification(
                    Component.text(revivePlayerName)
                            .decorate(TextDecoration.BOLD),
                    Component.text("Has been revived")
                            .color(NamedTextColor.GREEN),
                    player);
        }

        try {

            Team.deadTeams.remove(Team.getTeam(revivePlayerName).getName());

        } catch (Exception ignored) {}

        if (revivePlayer != null) {
            revivePlayer.setGameMode(GameMode.SURVIVAL);
            revivePlayer.teleport(PluginVariables.overWorld.getSpawnLocation());
            StartElytraHandler.giveStartElytra(revivePlayer);
        } else {
            PlayerVariables.addReviveQueue(revivePlayerName);
        }

        returnSuccess(revivePlayerName + " has been revived!", sender);
    }
}
