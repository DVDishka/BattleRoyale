package ru.dvdishka.battleroyale.handlers.commands.startbox;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.common.ConfigVariables;
import ru.dvdishka.battleroyale.logic.Scheduler;
import ru.dvdishka.battleroyale.logic.common.GameVariables;
import ru.dvdishka.battleroyale.logic.common.PluginVariables;

public class CreateStartBoxCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        for (World world : Bukkit.getWorlds()) {
            world.setPVP(false);
        }

        GameVariables.isStartBox = true;
        Location startBoxLocation = new Location(
                PluginVariables.overWorld,
                PluginVariables.overWorld.getSpawnLocation().getX(),
                ConfigVariables.startBoxY,
                PluginVariables.overWorld.getSpawnLocation().getZ());

        Chunk startBoxChunk = startBoxLocation.getChunk();

        int chunkX = startBoxChunk.getX() * 16;
        int chunkZ = startBoxChunk.getZ() * 16;

        Scheduler.getScheduler().runRegionTask(PluginVariables.plugin, startBoxLocation, (scheduledTask) -> {

            for (int x = chunkX; x < chunkX + 16; x++) {
                for (int z = chunkZ; z < chunkZ + 16; z++) {

                    new Location(PluginVariables.overWorld, x, ConfigVariables.startBoxY, z).getBlock().setType(Material.BEDROCK);
                }
            }

            for (int y = ConfigVariables.startBoxY; y < ConfigVariables.startBoxY + 7; y++) {

                for (int x = chunkX; x < chunkX + 16; x++) {
                    new Location(PluginVariables.overWorld, x, y, chunkZ).getBlock().setType(Material.BEDROCK);
                }

                for (int x = chunkX; x < chunkX + 16; x++) {
                    new Location(PluginVariables.overWorld, x, y, chunkZ + 15).getBlock().setType(Material.BEDROCK);
                }

                for (int z = chunkZ; z < chunkZ + 16; z++) {
                    new Location(PluginVariables.overWorld, chunkX, y, z).getBlock().setType(Material.BEDROCK);
                }

                for (int z = chunkZ; z < chunkZ + 16; z++) {
                    new Location(PluginVariables.overWorld, chunkX + 15, y, z).getBlock().setType(Material.BEDROCK);
                }
            }
        });

        Scheduler.getScheduler().runSync(PluginVariables.plugin, (scheduledTask) -> {
            PluginVariables.overWorld.setSpawnLocation(chunkX + 8, ConfigVariables.startBoxY + 1, chunkZ + 8);
            PluginVariables.overWorld.setGameRule(GameRule.SPAWN_RADIUS, 1);
        });

        for (Player player : Bukkit.getOnlinePlayers()) {

            Scheduler.getScheduler().runPlayerTask(PluginVariables.plugin, player, (scheduledTask) -> {
                player.teleportAsync(new Location(PluginVariables.overWorld, chunkX + 8, ConfigVariables.startBoxY + 1, chunkZ + 8));
            });
        }

        returnSuccess("Start box has been created successfully", sender);
    }
}
