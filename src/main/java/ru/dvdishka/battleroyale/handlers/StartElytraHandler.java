package ru.dvdishka.battleroyale.handlers;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.Scheduler;

import java.util.HashMap;

public class StartElytraHandler implements Listener {

    private static HashMap<String, Boolean> startElytraEnabled = new HashMap<>();
    private static HashMap<String, ScheduledTask> banPlayerElytraTasks = new HashMap<>();

    public static void giveEveryoneStartElytra() {

        for (String playerName : Common.players) {

            try {

                Player player = Bukkit.getPlayer(playerName);

                try {
                    banPlayerElytraTasks.get(player.getName()).cancel();
                } catch (Exception ignored) {}

                startElytraEnabled.put(playerName, Boolean.TRUE);

                ItemStack startElytra = new ItemStack(Material.ELYTRA);
                ItemMeta startElytraMeta = startElytra.getItemMeta();

                startElytraMeta.getPersistentDataContainer()
                        .set(NamespacedKey.fromString("start_elytra"), PersistentDataType.BOOLEAN, true);
                startElytraMeta.displayName(Component
                        .text("hang glider")
                        .color(NamedTextColor.LIGHT_PURPLE)
                        .decorate(TextDecoration.BOLD));
                startElytra.setItemMeta(startElytraMeta);

                {
                    Component header = Component.empty();
                    Component text = Component.empty();

                    header = header
                            .append(Component.text("HANG GLIDER")
                                    .color(NamedTextColor.LIGHT_PURPLE)
                                    .decorate(TextDecoration.BOLD));
                    text = text
                            .append(Component.text("You got a"))
                            .append(Component.space())
                            .append(Component.text("HANG GLIDER")
                                    .color(NamedTextColor.GOLD))
                            .append(Component.newline())
                            .append(Component.text("-".repeat(27))
                                    .color(NamedTextColor.YELLOW))
                            .append(Component.newline())
                            .append(Component.text("It will be deleted from your inventory after landing")
                                    .color(NamedTextColor.RED));

                    Common.sendNotification(header, text, player);
                }

                player.getInventory().setChestplate(startElytra);

            } catch (Exception ignored) {}
        }
    }

    public static void giveStartElytra(Player player) {

        try {
            banPlayerElytraTasks.get(player.getName()).cancel();
        } catch (Exception ignored) {}

        startElytraEnabled.put(player.getName(), Boolean.TRUE);

        ItemStack startElytra = new ItemStack(Material.ELYTRA);
        ItemMeta startElytraMeta = startElytra.getItemMeta();

        startElytraMeta.getPersistentDataContainer()
                .set(NamespacedKey.fromString("start_elytra"), PersistentDataType.BOOLEAN, true);
        startElytraMeta.displayName(Component
                .text("hang glider")
                .color(NamedTextColor.LIGHT_PURPLE)
                .decorate(TextDecoration.BOLD));
        startElytra.setItemMeta(startElytraMeta);

        {
            Component header = Component.empty();
            Component text = Component.empty();

            header = header
                    .append(Component.text("HANG GLIDER")
                            .color(NamedTextColor.LIGHT_PURPLE)
                            .decorate(TextDecoration.BOLD));
            text = text
                    .append(Component.text("You got a"))
                    .append(Component.space())
                    .append(Component.text("HANG GLIDER")
                            .color(NamedTextColor.GOLD))
                    .append(Component.newline())
                    .append(Component.text("-".repeat(27))
                            .color(NamedTextColor.YELLOW))
                    .append(Component.newline())
                    .append(Component.text("it will be deleted from your inventory after landing")
                            .color(NamedTextColor.RED));

            Common.sendNotification(header, text, player);
        }

        player.getInventory().setChestplate(startElytra);
    }

    @EventHandler
    public static void onElytraUse(EntityToggleGlideEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (startElytraEnabled.getOrDefault(event.getEntity().getName(), Boolean.FALSE) && event.isGliding()) {

            final String playerName = event.getEntity().getName();

            ScheduledTask banPlayerElytraTask = Scheduler.getScheduler().runSyncDelayed(Common.plugin, (scheduledTask) -> {
                StartElytraHandler.startElytraEnabled.put(playerName, Boolean.FALSE);
            }, 1800);

            banPlayerElytraTasks.put(playerName, banPlayerElytraTask);
        }

        if (!startElytraEnabled.getOrDefault(event.getEntity().getName(), Boolean.FALSE) || !event.isGliding()) {

            try {

                Player player = (Player) event.getEntity();

                if (player.getInventory().getChestplate().getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("start_elytra"))) {
                    event.setCancelled(true);
                    player.getInventory().setChestplate(null);

                    {
                        Component header = Component.empty();
                        Component text = Component.empty();

                        header = header
                                .append(Component.text("HANG GLIDER")
                                        .color(NamedTextColor.LIGHT_PURPLE)
                                        .decorate(TextDecoration.BOLD));

                        text = text
                                .append(Component.text("HANG GLIDER")
                                        .color(NamedTextColor.GOLD))
                                .append(Component.space())
                                .append(Component.text("has been removed from your inventory")
                                        .color(NamedTextColor.RED));

                        Common.sendNotification(header, text, player);
                    }
                }

            } catch (Exception ignored) {}
        }
    }
}
