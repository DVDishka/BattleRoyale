package ru.dvdishka.battleroyale.logic.classes.drop;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import ru.dvdishka.battleroyale.logic.common.PluginVariables;

public class DropContainerInventory implements InventoryHolder {

    private final Inventory inventory;

    public DropContainerInventory() {
        this.inventory = PluginVariables.plugin.getServer().createInventory(this, 54, Component
                .text("Drop container")
                .color(NamedTextColor.LIGHT_PURPLE));
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
