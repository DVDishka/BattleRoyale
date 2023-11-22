package ru.dvdishka.battleroyale.logic.classes.drop;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ru.dvdishka.battleroyale.logic.ConfigVariables;
import ru.dvdishka.battleroyale.logic.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.*;


public class DropType implements ConfigurationSerializable {

    private ArrayList<ItemStack> items = new ArrayList<>();

    private static ArrayList<DropType> dropTypes = new ArrayList<>();

    private DropType(ArrayList<ItemStack> items) {
        this.items = items;
    }

    public static DropType getRandomType() {
        if (dropTypes.isEmpty()) {
            return null;
        }
        return dropTypes.get(new Random().nextInt(0, dropTypes.size()));
    }

    public ArrayList<ItemStack> getItems() {
        return items;
    }

    public static void deserialize(ConfigurationSection dropTypeConfig) {

        Map<String, Object> serializedItems = dropTypeConfig.getConfigurationSection("items").getValues(false);

        ArrayList<ItemStack> deserializedItems = new ArrayList<>();

        for (Map.Entry<String, Object> serializedItemMap : serializedItems.entrySet()) {

            Map<String, Object> serializedItem = dropTypeConfig.getConfigurationSection("items." + serializedItemMap.getKey()).getValues(false);

            Material itemMaterial = Material.getMaterial(((String) serializedItem.get("material")).toUpperCase());
            int itemAmount = (int) serializedItem.get("amount");

            if (itemMaterial == null) {
                Logger.getLogger().warn("Wrong material name in " + ConfigVariables.dropTypesFile + ": " + (String) serializedItem.get("material"));
            }
            assert itemMaterial != null;

            ItemStack deserializedItem = new ItemStack(itemMaterial, itemAmount);

            Map<String, Object> serializedItemEnchantments = dropTypeConfig.getConfigurationSection("items." + serializedItemMap.getKey() + ".enchantments").getValues(false);

            for (Map.Entry<String, Object> serializedEnchantment : serializedItemEnchantments.entrySet()) {

                Enchantment itemDeserializedEnchantment = Enchantment.getByKey(NamespacedKey.minecraft(((String) dropTypeConfig.getConfigurationSection("items." + serializedItemMap.getKey() + ".enchantments." + serializedEnchantment.getKey()).get("name")).toLowerCase()));
                int itemDeserializedEnchantmentLevel = (int) dropTypeConfig.getConfigurationSection("items." + serializedItemMap.getKey() + ".enchantments." + serializedEnchantment.getKey()).get("level");

                ItemMeta itemMeta = deserializedItem.getItemMeta();
                itemMeta.addEnchant(itemDeserializedEnchantment, itemDeserializedEnchantmentLevel, true);

                deserializedItem.setItemMeta(itemMeta);
            }

            deserializedItems.add(deserializedItem);
        }

        dropTypes.add(new DropType(deserializedItems));
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

                serializedEnchantment.put("name", enchantment.getKey().getKey().getKey());
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