package ru.dvdishka.battleroyale.handlers;

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

public class StartElytraHandler implements Listener {

    private static boolean startElytraEnabled = false;

    public static void giveStartElytra() {

        startElytraEnabled = true;

        for (String playerName : Common.players) {

            try {

                Player player = Bukkit.getPlayer(playerName);

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
                    Component message = Component.empty();

                    message = message
                            .append(Component.newline())
                            .append(Component.text("-".repeat(26))
                                    .color(NamedTextColor.RED)
                                    .decorate(TextDecoration.BOLD))
                            .append(Component.newline());

                    message = message
                            .append(Component.text("HANG GLIDER")
                                    .color(NamedTextColor.LIGHT_PURPLE)
                                    .decorate(TextDecoration.BOLD))
                            .append(Component.newline());

                    message = message
                            .append(Component.text("-".repeat(27))
                                    .color(NamedTextColor.YELLOW))
                            .append(Component.newline());

                    message = message
                            .append(Component.text("You got a"))
                            .append(Component.space())
                            .append(Component.text("HANG GLIDER")
                                    .color(NamedTextColor.GOLD))
                            .append(Component.newline())
                            .append(Component.text("-".repeat(27))
                                    .color(NamedTextColor.YELLOW))
                            .append(Component.newline())
                            .append(Component.text("it will be deleted from your inventory after landing")
                                    .color(NamedTextColor.RED))
                            .append(Component.newline());

                    message = message
                            .append(Component.text("-".repeat(26))
                                    .color(NamedTextColor.RED)
                                    .decorate(TextDecoration.BOLD))
                            .append(Component.newline());

                    player.sendMessage(message);
                    Common.notificationSound(player);
                }

                player.getInventory().setChestplate(startElytra);

            } catch (Exception ignored) {}
        }

        Scheduler.getScheduler().runSyncDelayed(Common.plugin, (scheduledTask) -> {
            StartElytraHandler.startElytraEnabled = false;
        }, 1800);
    }

    @EventHandler
    public static void onElytraUse(EntityToggleGlideEvent event) {

        if (!startElytraEnabled || !event.isGliding()) {

            try {

                Player player = (Player) event.getEntity();

                if (player.getInventory().getChestplate().getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("start_elytra"))) {
                    event.setCancelled(true);
                    player.getInventory().setChestplate(null);

                    {
                        Component message = Component.empty();

                        message = message
                                .append(Component.newline())
                                .append(Component.text("-".repeat(26))
                                        .color(NamedTextColor.RED)
                                        .decorate(TextDecoration.BOLD))
                                .append(Component.newline());

                        message = message
                                .append(Component.text("HANG GLIDER")
                                        .color(NamedTextColor.LIGHT_PURPLE)
                                        .decorate(TextDecoration.BOLD))
                                .append(Component.newline());

                        message = message
                                .append(Component.text("-".repeat(27))
                                        .color(NamedTextColor.YELLOW))
                                .append(Component.newline());

                        message = message
                                .append(Component.text("HANG GLIDER")
                                        .color(NamedTextColor.GOLD))
                                .append(Component.space())
                                .append(Component.text("has been removed from your inventory")
                                        .color(NamedTextColor.RED))
                                .append(Component.newline());

                        message = message
                                .append(Component.text("-".repeat(26))
                                        .color(NamedTextColor.RED)
                                        .decorate(TextDecoration.BOLD))
                                .append(Component.newline());

                        player.sendMessage(message);
                        Common.notificationSound(player);
                    }
                }

            } catch (Exception ignored) {}
        }
    }
}
