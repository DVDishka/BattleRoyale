package ru.dvdishka.battleroyale.logic.classes.drop;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.dvdishka.battleroyale.logic.common.ConfigVariables;
import ru.dvdishka.battleroyale.logic.Logger;

import java.util.ArrayList;
import java.util.Random;
import java.util.*;


public class DropType {

    private final String name;
    private final ArrayList<ItemStack> items;

    private static final ArrayList<DropType> dropTypes = new ArrayList<>();

    private DropType(ArrayList<ItemStack> items, String name) {
        this.items = items;
        this.name = name;
    }

    public static DropType getByNameOrRandom(String name) {

        for (DropType dropType : dropTypes) {
            if (dropType.name.equals(name)) {
                return dropType;
            }
        }

        return getRandomType();
    }

    public static DropType getRandomType() {
        if (dropTypes.isEmpty()) {
            return null;
        }
        return dropTypes.get(new Random().nextInt(0, dropTypes.size()));
    }

    public static ArrayList<DropType> getDropTypes() {
        return dropTypes;
    }

    public ArrayList<ItemStack> getItems() {
        return this.items;
    }

    public String getName() {
        return this.name;
    }

    public static void deserialize(ConfigurationSection dropTypeConfig, String dropTypeName) {

        Map<String, Object> serializedItems = dropTypeConfig.getConfigurationSection("items").getValues(false);

        ArrayList<ItemStack> deserializedItems = new ArrayList<>();

        for (Map.Entry<String, Object> serializedItemMap : serializedItems.entrySet()) {

            Map<String, Object> serializedItem = dropTypeConfig.getConfigurationSection("items." + serializedItemMap.getKey()).getValues(false);

            Material itemMaterial = Material.getMaterial(((String) serializedItem.get("material")).toUpperCase());

            int itemAmount = 1;
            try {
                itemAmount = (int) serializedItem.get("amount");
            } catch (Exception ignored) {}

            if (itemMaterial == null) {
                Logger.getLogger().warn("Wrong material name in " + ConfigVariables.dropTypesFile + ": " + (String) serializedItem.get("material"));
                continue;
            }

            ItemStack deserializedItem = new ItemStack(itemMaterial, itemAmount);

            try {
                Map<String, Object> serializedItemEnchantments = dropTypeConfig.getConfigurationSection("items." + serializedItemMap.getKey() + ".enchantments").getValues(false);

                for (Map.Entry<String, Object> serializedEnchantment : serializedItemEnchantments.entrySet()) {

                    Enchantment itemDeserializedEnchantment = Enchantment.getByKey(NamespacedKey.minecraft(((String) dropTypeConfig.getConfigurationSection("items." + serializedItemMap.getKey() + ".enchantments." + serializedEnchantment.getKey()).get("name")).toLowerCase()));

                    int itemDeserializedEnchantmentLevel = 1;
                    try {
                        itemDeserializedEnchantmentLevel = (int) dropTypeConfig.getConfigurationSection("items." + serializedItemMap.getKey() + ".enchantments." + serializedEnchantment.getKey()).get("level");
                    } catch (Exception ignored) {}

                    ItemMeta itemMeta = deserializedItem.getItemMeta();
                    itemMeta.addEnchant(itemDeserializedEnchantment, itemDeserializedEnchantmentLevel, true);

                    deserializedItem.setItemMeta(itemMeta);
                }
            } catch (Exception ignored) {}

            try {
                Map<String, Object> serializedItemEffects = dropTypeConfig.getConfigurationSection("items." + serializedItemMap.getKey() + ".effects").getValues(false);

                for (Map.Entry<String, Object> serializedEffect : serializedItemEffects.entrySet()) {

                    PotionEffectType itemDeserializedEffect = PotionEffectType.getByKey(NamespacedKey.minecraft(((String) dropTypeConfig.getConfigurationSection("items." + serializedItemMap.getKey() + ".effects." + serializedEffect.getKey()).get("name")).toLowerCase()));

                    int itemDeserializedEffectLevel = 0;
                    try {
                        itemDeserializedEffectLevel = -1 + (int) dropTypeConfig.getConfigurationSection("items." + serializedItemMap.getKey() + ".effects." + serializedEffect.getKey()).get("level");
                    } catch (Exception ignored) {}

                    int itemDeserializedEffectDuration = 0;
                    try {
                        itemDeserializedEffectDuration = 20 * (int) dropTypeConfig.getConfigurationSection("items." + serializedItemMap.getKey() + ".effects." + serializedEffect.getKey()).get("duration");
                    } catch (Exception ignored) {}

                    PotionMeta itemMeta = (PotionMeta) deserializedItem.getItemMeta();
                    itemMeta.addCustomEffect(new PotionEffect(itemDeserializedEffect, itemDeserializedEffectDuration, itemDeserializedEffectLevel), false);

                    deserializedItem.setItemMeta(itemMeta);
                }
            } catch (Exception ignored) {}

            deserializedItems.add(deserializedItem);
        }

        dropTypes.add(new DropType(deserializedItems, dropTypeName));
    }
}