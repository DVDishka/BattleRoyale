package ru.dvdishka.battleroyale.commands.startbox;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import ru.dvdishka.battleroyale.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.common.Common;
import ru.dvdishka.battleroyale.common.ConfigVariables;
import ru.dvdishka.battleroyale.common.Scheduler;

public class Remove implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        Common.isStartBox = false;
        Location startBoxLocation = new Location(Bukkit.getWorld("world"),
                ConfigVariables.startBoxX, ConfigVariables.startBoxY, ConfigVariables.startBoxZ);

        Scheduler.getScheduler().runRegionTask(Common.plugin, startBoxLocation, (scheduledTask) -> {

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

        Scheduler.getScheduler().runSync(Common.plugin, (scheduledTask) -> {
            Bukkit.getWorld("world").setSpawnLocation(0, 0, 0);
            Bukkit.getWorld("world").setGameRule(GameRule.SPAWN_RADIUS, 0);
        });

        returnSuccess("Start box has been removed successfully!", sender);
    }
}
