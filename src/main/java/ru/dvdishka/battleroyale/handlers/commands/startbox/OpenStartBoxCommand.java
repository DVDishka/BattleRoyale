package ru.dvdishka.battleroyale.handlers.commands.startbox;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import ru.dvdishka.battleroyale.handlers.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.ConfigVariables;
import ru.dvdishka.battleroyale.logic.Scheduler;

public class OpenStartBoxCommand implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        Common.isStartBox = false;
        Location startBoxLocation = new Location(
                Common.overWorld,
                Common.overWorld.getSpawnLocation().getX(),
                ConfigVariables.startBoxY,
                Common.overWorld.getSpawnLocation().getZ());

        Scheduler.getScheduler().runRegionTask(Common.plugin, startBoxLocation, (scheduledTask) -> {

            Chunk startBoxChunk = startBoxLocation.getChunk();

            int chunkX = startBoxChunk.getX() * 16;
            int chunkZ = startBoxChunk.getZ() * 16;

            for (int y = ConfigVariables.startBoxY + 1; y < ConfigVariables.startBoxY + 7; y++) {

                for (int x = chunkX; x < chunkX + 16; x++) {
                    new Location(Common.overWorld, x, y, chunkZ).getBlock().setType(Material.AIR);
                }

                for (int x = chunkX; x < chunkX + 16; x++) {
                    new Location(Common.overWorld, x, y, chunkZ + 15).getBlock().setType(Material.AIR);
                }

                for (int z = chunkZ; z < chunkZ + 16; z++) {
                    new Location(Common.overWorld, chunkX, y, z).getBlock().setType(Material.AIR);
                }

                for (int z = chunkZ; z < chunkZ + 16; z++) {
                    new Location(Common.overWorld, chunkX + 15, y, z).getBlock().setType(Material.AIR);
                }
            }
        });

        returnSuccess("Start box has been opened successfully", sender);
    }
}
