package ru.dvdishka.battleroyale.logic.classes.drop;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import ru.dvdishka.battleroyale.logic.Common;

public class DropContainerInventory implements InventoryHolder {

    private final Inventory inventory;

    public DropContainerInventory() {
        this.inventory = Common.plugin.getServer().createInventory(this, 54);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
