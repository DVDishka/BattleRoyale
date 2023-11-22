package ru.dvdishka.battleroyale.logic.classes.drop;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;


public class DropType implements ConfigurationSerializable {

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

        ArrayList<DropType> types = new ArrayList<>();


    }

    @Override
    public @NotNull Map<String, Object> serialize() {

        HashMap<String, Object> map = new HashMap<>();

        ArrayList<Map<String, Object>> serializedItems = new ArrayList<>();

        for (ItemStack item : items) {

            HashMap<String, Object> serializedItem = new HashMap<>();

            serializedItem.put("material", item.getType().name());
            serializedItem.put("amount", item.getAmount());

            ArrayList<HashMap<String, Object>> serializedEnchantments = new ArrayList<>();

            for (Map.Entry<Enchantment, Integer> enchantment : item.getEnchantments().entrySet()) {

                HashMap<String, Object> serializedEnchantment = new HashMap<>();

                serializedEnchantment.put("name", enchantment.getKey().getName());
                serializedEnchantment.put("level", enchantment.getValue());

                serializedEnchantments.add(serializedEnchantment);
            }

            serializedItem.put("enchantments", serializedEnchantments);

            serializedItems.add(serializedItem);
        }

        map.put("items", serializedItems);

        return map;
    }
}