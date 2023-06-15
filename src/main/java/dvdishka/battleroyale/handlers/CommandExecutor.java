package dvdishka.battleroyale.handlers;

import dvdishka.battleroyale.classes.Team;
import dvdishka.battleroyale.common.CommonVariables;
import dvdishka.battleroyale.common.ConfigVariables;
import dvdishka.battleroyale.classes.SuperPower;
import dvdishka.battleroyale.classes.UpdateEvent;
import dvdishka.battleroyale.tasks.endless.EffectUpdateTask;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
                        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
                }, null);

                ArrayList<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());
                for (PotionEffect effect : effects) {
                    playerScheduler.run(CommonVariables.plugin, (task) -> {
                        player.removePotionEffect(effect.getType());
                    }, null);
                }

                CommonVariables.timer.addPlayer(player);

                playerScheduler.run(CommonVariables.plugin, (task) -> {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 200 , 1, false, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 1200, 9, false, false));
                }, null);
                int powerNumber = new Random().nextInt(0, SuperPower.values().length);
                SuperPower.values()[powerNumber].setToPlayer(player);
            }
            Bukkit.getPluginManager().callEvent(new UpdateEvent());

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

            CommonVariables.resetVariables();

            return true;
        }

        if (commandName.equals("team") && args.length == 3 && args[1].equals("create")) {

            if (CommonVariables.isGameStarted) {

                sender.sendMessage(Component.text("You can not create team while the game is on!")
                        .color(TextColor.color(222, 16, 23)));
                return false;
            }

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

            if (CommonVariables.isGameStarted) {

                sender.sendMessage(Component.text("You can not invite while the game is on!")
                        .color(TextColor.color(222, 16, 23)));
                return false;
            }

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

            if (Team.getTeam(invitedPlayer) != null && Team.getTeam(invitedPlayer).getName().equals(playerTeam.getName())) {

                sender.sendMessage(Component.text("This player is already in your team!")
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

            return true;
        }

        if (commandName.equals("accept") && args.length == 3 && CommonVariables.invites.get(args[2]).contains(args[1])) {

            if (CommonVariables.isGameStarted) {

                sender.sendMessage(Component.text("You can not accept an invitation while the game is on!")
                        .color(TextColor.color(222, 16, 23)));
                return false;
            }

            String newMemberName = args[1];
            String newTeamName = args[2];
            CommonVariables.invites.get(args[2]).remove(args[1]);
            Player newMember = Bukkit.getPlayer(args[1]);
            Team oldTeam = Team.getTeam(args[1]);
            Team newTeam = Team.get(args[2]);

            newTeam.addPlayer(args[1]);

            if (oldTeam != null) {

                oldTeam.removePlayer(newMemberName);

                if (newMember != null) {

                    newMember.sendMessage(Component.text("You left " + oldTeam.getName())
                            .color(TextColor.color(222, 16, 23)));
                }
            }

            if (newMember != null) {

                newMember.sendMessage(Component.text("You has joined " + args[2])
                        .color(TextColor.color(0, 234, 53)));
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.getName().equals(newMemberName)) {
                    player.sendMessage(Component.text(newMemberName + " has joined " + newTeamName + "!")
                            .color(newTeam.getColor()));
                }
            }

            return true;
        }

        if (commandName.equals("cancel") && CommonVariables.invites.get(args[2]).contains(args[1])) {

            CommonVariables.invites.get(args[2]).remove(args[1]);

            return true;
        }

        if (commandName.equals("team") && args.length == 2 && args[1].equals("leave")) {

            if (CommonVariables.isGameStarted) {

                sender.sendMessage(Component.text("You can not leave while the game is on!")
                        .color(TextColor.color(222, 16, 23)));
                return false;
            }

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

        if (commandName.equals("team") && args.length == 2 && args[1].equals("list")) {

            TextComponent.Builder fullMessage = Component.text();

            fullMessage.append(Component.text("Teams:")
                    .color(TextColor.color(187, 150, 0)));
            fullMessage.appendNewline();

            for (Team team : Team.teams) {

                Component teamMessagePart = Component.text("Team name: " + team.getName())
                        .color(team.getColor())
                        .appendNewline();
                teamMessagePart = teamMessagePart.append(Component.text("Members number: " + team.getPlayers().size())
                        .color(team.getColor())
                        .appendNewline());
                teamMessagePart = teamMessagePart.append(Component.text("Members: ")
                        .color(team.getColor()));
                for (String member : team.getPlayers()) {
                    teamMessagePart = teamMessagePart.append(Component.text(member + " ")
                            .color(team.getColor()));
                }
                fullMessage.append(teamMessagePart);
                fullMessage.appendNewline();
                fullMessage.appendNewline();
            }

            fullMessage.append(Component.text("Solo: ")
                    .color(TextColor.color(187, 50, 0)));
            fullMessage.appendNewline();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Team.getTeam(player) == null) {
                    fullMessage.append(Component.text(player.getName() + " "));
                }
            }

            sender.sendMessage(fullMessage);

            return true;
        }

        if (commandName.equals("startBox") && args.length == 2 && args[1].equals("create") && sender.isOp()) {

            CommonVariables.isStartBox = true;
            Location startBoxLocation = new Location(Bukkit.getWorld("world"),
                    ConfigVariables.startBoxX, ConfigVariables.startBoxY, ConfigVariables.startBoxZ);

            Chunk startBoxChunk = startBoxLocation.getChunk();

            int chunkX = startBoxChunk.getX() * 16;
            int chunkZ = startBoxChunk.getZ() * 16;

            Bukkit.getRegionScheduler().run(CommonVariables.plugin, startBoxLocation, (task) -> {

                for (int x = chunkX; x < chunkX + 16; x++) {
                    for (int z = chunkZ; z < chunkZ + 16; z++) {

                        new Location(Bukkit.getWorld("world"), x, ConfigVariables.startBoxY, z).getBlock().setType(Material.BEDROCK);
                    }
                }

                for (int y = ConfigVariables.startBoxY; y < ConfigVariables.startBoxY + 7; y++) {

                    for (int x = chunkX; x < chunkX + 16; x++) {
                        new Location(Bukkit.getWorld("world"), x, y, chunkZ).getBlock().setType(Material.BEDROCK);
                    }

                    for (int x = chunkX; x < chunkX + 16; x++) {
                        new Location(Bukkit.getWorld("world"), x, y, chunkZ + 15).getBlock().setType(Material.BEDROCK);
                    }

                    for (int z = chunkZ; z < chunkZ + 16; z++) {
                        new Location(Bukkit.getWorld("world"), chunkX, y, z).getBlock().setType(Material.BEDROCK);
                    }

                    for (int z = chunkZ; z < chunkZ + 16; z++) {
                        new Location(Bukkit.getWorld("world"), chunkX + 15, y, z).getBlock().setType(Material.BEDROCK);
                    }
                }
            });

            Bukkit.getGlobalRegionScheduler().run(CommonVariables.plugin, (task) -> {
                Bukkit.getWorld("world").setSpawnLocation(chunkX + 8, ConfigVariables.startBoxY + 1, chunkZ + 8);
                Bukkit.getWorld("world").setGameRule(GameRule.SPAWN_RADIUS, 1);
            });

            for (Player player : Bukkit.getOnlinePlayers()) {
                EntityScheduler playerScheduler = player.getScheduler();

                playerScheduler.run(CommonVariables.plugin, (task) -> {
                    player.teleportAsync(new Location(Bukkit.getWorld("world"), chunkX + 8, ConfigVariables.startBoxY + 1, chunkZ + 8));
                }, null);
            }

            sender.sendMessage(Component.text("Start box has been created successfully!")
                    .color(TextColor.color(0, 234, 53)));

            return true;
        }

        if (commandName.equals("startBox") && args.length == 2 && args[1].equals("remove") && sender.isOp()) {

            CommonVariables.isStartBox = false;
            Location startBoxLocation = new Location(Bukkit.getWorld("world"),
                    ConfigVariables.startBoxX, ConfigVariables.startBoxY, ConfigVariables.startBoxZ);

            Bukkit.getRegionScheduler().run(CommonVariables.plugin, startBoxLocation, (task) -> {

                Chunk startBoxChunk = startBoxLocation.getChunk();

                int chunkX = startBoxChunk.getX() * 16;
                int chunkZ = startBoxChunk.getZ() * 16;

                for (int x = chunkX; x < chunkX + 16; x++) {
                    for (int z = chunkZ; z < chunkZ + 16; z++) {

                        new Location(Bukkit.getWorld("world"), x, ConfigVariables.startBoxY, z).getBlock().setType(Material.AIR);
                    }
                }

                for (int y = ConfigVariables.startBoxY; y < ConfigVariables.startBoxY + 7; y++) {

                    for (int x = chunkX; x < chunkX + 16; x++) {
                        new Location(Bukkit.getWorld("world"), x, y, chunkZ).getBlock().setType(Material.AIR);
                    }

                    for (int x = chunkX; x < chunkX + 16; x++) {
                        new Location(Bukkit.getWorld("world"), x, y, chunkZ + 15).getBlock().setType(Material.AIR);
                    }

                    for (int z = chunkZ; z < chunkZ + 16; z++) {
                        new Location(Bukkit.getWorld("world"), chunkX, y, z).getBlock().setType(Material.AIR);
                    }

                    for (int z = chunkZ; z < chunkZ + 16; z++) {
                        new Location(Bukkit.getWorld("world"), chunkX + 15, y, z).getBlock().setType(Material.AIR);
                    }
                }
            });

            Bukkit.getGlobalRegionScheduler().run(CommonVariables.plugin, (task) -> {
                Bukkit.getWorld("world").setSpawnLocation(0, 0, 0);
                Bukkit.getWorld("world").setGameRule(GameRule.SPAWN_RADIUS, 0);
            });

            sender.sendMessage(Component.text("Start box has been removed successfully!")
                    .color(TextColor.color(0, 234, 53)));

            return true;
        }

        sender.sendMessage(Component.text("Unknown Command!")
                .color(TextColor.color(222, 16, 23)));
        return false;
    }
}
