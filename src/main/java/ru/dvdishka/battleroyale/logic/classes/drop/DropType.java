package ru.dvdishka.battleroyale.logic.classes.drop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DropType {

    private ArrayList<ItemStack> items = new ArrayList<>();

    private static ArrayList<DropType> dropTypes = new ArrayList<>();

    private DropType(ArrayList<ItemStack> items) {
        this.items = items;
    }

    public static DropType getRandomType() {
        return dropTypes.get(new Random().nextInt(0, dropTypes.size()));
    }

    public ArrayList<ItemStack> getItems() {
        return items;
    }

    public static void deserialize(File file) {

        dropTypes.add(new DropType(new ArrayList<>(List.of(new ItemStack(Material.ACACIA_BOAT)))));
    }
}
