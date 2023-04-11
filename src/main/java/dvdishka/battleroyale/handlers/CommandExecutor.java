package dvdishka.battleroyale.handlers;

import dvdishka.battleroyale.classes.Team;
import dvdishka.battleroyale.common.CommonVariables;
import dvdishka.battleroyale.common.ConfigVariables;
import dvdishka.battleroyale.classes.SuperPowers;
import dvdishka.battleroyale.classes.UpdateEvent;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.emitter.Emitter;

import java.util.ArrayList;
import java.util.Random;

public class CommandExecutor implements org.bukkit.command.CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!command.getName().equals("battleroyale") || args.length < 1) {

            sender.sendMessage(ChatColor.RED + "Unknown command!");
            return false;
        }

        String commandName = args[0];

        if (commandName.equals("start")) {

            CommonVariables.isGameStarted = true;
            CommonVariables.zoneStage = 0;
            CommonVariables.isFinalZone = false;
            CommonVariables.isZoneMove = false;
            CommonVariables.deadPlayers.clear();
            CommonVariables.players.clear();

            CommonVariables.timer.setVisible(true);

            for (World world : Bukkit.getWorlds()) {

                world.setPVP(false);
                world.getWorldBorder().setCenter(0, 0);

                if (world.getWorldBorder().getSize()  != ConfigVariables.defaultWorldBorderDiameter) {

                    world.getWorldBorder().setSize(ConfigVariables.defaultWorldBorderDiameter, 1);

                } else {

                    world.getWorldBorder().setSize(ConfigVariables.defaultWorldBorderDiameter + 1, 1);
                }
            }

            for (Player player : Bukkit.getOnlinePlayers()) {

                CommonVariables.players.add(player.getName());

                EntityScheduler playerScheduler = player.getScheduler();

                playerScheduler.run(CommonVariables.plugin, (task) -> {
                        player.setHealth(player.getMaxHealth());
                }, null);

                ArrayList<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());
                for (PotionEffect effect : effects) {
                    playerScheduler.run(CommonVariables.plugin, (task) -> {
                        player.removePotionEffect(effect.getType());
                    }, null);
                }

                CommonVariables.timer.addPlayer(player);

                playerScheduler.run(CommonVariables.plugin, (task) -> {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 10 , 1, false, false));
                }, null);
                int powerNumber = new Random().nextInt(0, SuperPowers.values().length);
                SuperPowers.values()[powerNumber].setToPlayer(player);

                Bukkit.getPluginManager().callEvent(new UpdateEvent());
            }

            return true;
        }

        if (commandName.equals("stop")) {

            Bukkit.getGlobalRegionScheduler().cancelTasks(CommonVariables.plugin);
            Bukkit.getAsyncScheduler().cancelTasks(CommonVariables.plugin);

            CommonVariables.timer.setVisible(false);

            for (World world : Bukkit.getWorlds()) {
                world.setPVP(true);
                world.getWorldBorder().setCenter(0, 0);
                world.getWorldBorder().setSize(ConfigVariables.defaultWorldBorderDiameter);
            }

            CommonVariables.isGameStarted = true;
            CommonVariables.zoneStage = 0;
            CommonVariables.isFinalZone = false;
            CommonVariables.isZoneMove = false;
            CommonVariables.deadPlayers.clear();
            CommonVariables.players.clear();

            return true;
        }

        if (commandName.equals("team") && args.length == 3 && args[1].equals("create")) {

            String teamName = args[2];

            if (Team.getTeam(sender.getName()) != null) {
                sender.sendMessage(Component.text("You are already in the team!")
                        .color(TextColor.color(222, 16, 23)));
                return false;
            }

            for (Team team : Team.teams) {
                if (team.getName().equals(teamName)) {
                    sender.sendMessage(Component.text("Team with that name already exist!")
                            .color(TextColor.color(222, 16, 23)));
                    return false;
                }
            }

            Team newTeam = new Team(args[2], sender.getName());

            newTeam.addPlayer(sender.getName());

            sender.sendMessage(Component.text("Team " + teamName + " has been created!")
                    .color(TextColor.color(0, 234, 53)));
            return true;
        }

        if (commandName.equals("team") && args.length == 3 && args[1].equals("invite")) {

            Team playerTeam = Team.getTeam(sender.getName());

            if (playerTeam == null || !playerTeam.isLeader(sender.getName())) {
                sender.sendMessage(Component.text("You are not in the team or you are not the leader!")
                        .color(TextColor.color(222, 16, 23)));
                return false;
            }

            Player invitedPlayer = Bukkit.getPlayer(args[2]);

            if (invitedPlayer == null) {
                sender.sendMessage(Component.text("There is no player with that name!")
                        .color(TextColor.color(222, 16, 23)));
                return false;
            }

            CommonVariables.invites.get(playerTeam.getName()).add(invitedPlayer.getName());

            invitedPlayer.sendMessage(Component.text()
                            .append(Component.text("You has been invited to ")
                                    .color(TextColor.color(0, 143, 234))
                            )
                            .append(Component.text(playerTeam.getName())
                                    .decorate(TextDecoration.BOLD)
                                    .color(TextColor.color(201, 0, 238))
                            )
                            .appendNewline()
                            .append(Component.text("[ACCEPT]")
                                    .color(TextColor.color(0, 234, 53))
                                    .decorate(TextDecoration.BOLD)
                                    .clickEvent(ClickEvent.runCommand("/battleroyale accept " + invitedPlayer.getName() + " " + playerTeam.getName()))
                            )
                            .appendSpace()
                            .append(Component.text("[DECLINE]")
                                    .color(TextColor.color(222, 16, 23))
                                    .decorate(TextDecoration.BOLD)
                                    .clickEvent(ClickEvent.runCommand("/battleroyale cancel " + invitedPlayer.getName() + " " + playerTeam.getName()))
                            )
                    .build()
            );
        }

        if (commandName.equals("accept") && args.length == 3 && CommonVariables.invites.get(args[2]).contains(args[1])) {

            CommonVariables.invites.get(args[2]).remove(args[1]);
            Player newMember = Bukkit.getPlayer(args[1]);
            Team oldTeam = Team.getTeam(args[1]);
            Team.get(args[2]).addPlayer(args[1]);

            if (oldTeam != null) {

                oldTeam.removePlayer(args[1]);

                if (newMember != null) {

                    newMember.sendMessage(Component.text("You left " + oldTeam.getName())
                            .color(TextColor.color(222, 16, 23)));
                }
            }

            if (newMember != null) {

                newMember.sendMessage(Component.text("You has joined " + args[2])
                        .color(TextColor.color(0, 234, 53)));
            }

            return true;
        }

        if (commandName.equals("cancel") && CommonVariables.invites.get(args[2]).contains(args[1])) {

            CommonVariables.invites.get(args[2]).remove(args[1]);

            return true;
        }

        if (commandName.equals("team") && args.length == 2 && args[1].equals("leave")) {

            Team playerTeam = Team.getTeam(sender.getName());

            if (playerTeam == null) {
                sender.sendMessage(Component.text("You are not in the team!")
                        .color(TextColor.color(222, 16, 23)));
                return false;
            }

            if (playerTeam.isLeader(sender.getName())) {

                for (String member : playerTeam.getPlayers()) {

                    Player player = Bukkit.getPlayer(member);
                    EntityScheduler playerScheduler = player.getScheduler();

                    playerScheduler.run(CommonVariables.plugin, (task) -> {
                        player.displayName(null);
                    }, null);
                }

                playerTeam.setPlayers(new ArrayList<>());

                Team.teams.remove(playerTeam);
            }
            else {
                playerTeam.removePlayer(sender.getName());
            }

            return true;
        }

        return false;
    }
}
