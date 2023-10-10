package ru.dvdishka.battleroyale.commands.startbox;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.commands.common.CommandInterface;
import ru.dvdishka.battleroyale.common.Common;
import ru.dvdishka.battleroyale.common.ConfigVariables;
import ru.dvdishka.battleroyale.common.Scheduler;

public class Create implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        Common.isStartBox = true;
        Location startBoxLocation = new Location(Bukkit.getWorld("world"),
                ConfigVariables.startBoxX, ConfigVariables.startBoxY, ConfigVariables.startBoxZ);

        Chunk startBoxChunk = startBoxLocation.getChunk();

        int chunkX = startBoxChunk.getX() * 16;
        int chunkZ = startBoxChunk.getZ() * 16;

        Scheduler.getScheduler().runRegionTask(Common.plugin, startBoxLocation, () -> {

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

        Scheduler.getScheduler().runSync(Common.plugin, () -> {
            Bukkit.getWorld("world").setSpawnLocation(chunkX + 8, ConfigVariables.startBoxY + 1, chunkZ + 8);
            Bukkit.getWorld("world").setGameRule(GameRule.SPAWN_RADIUS, 1);
        });

        for (Player player : Bukkit.getOnlinePlayers()) {

            Scheduler.getScheduler().runPlayerTask(Common.plugin, player, () -> {
                player.teleportAsync(new Location(Bukkit.getWorld("world"), chunkX + 8, ConfigVariables.startBoxY + 1, chunkZ + 8));
            });
        }

        returnSuccess("Start box has been created successfully!", sender);
    }
}
