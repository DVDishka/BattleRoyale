package ru.dvdishka.battleroyale.logic.classes.superpower;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import ru.dvdishka.battleroyale.logic.common.Common;
import ru.dvdishka.battleroyale.logic.Logger;

import java.util.*;

public class SuperPower {

    private static final HashMap<String, SuperPower> playerSuperPowers = new HashMap<>();

    private final List<PotionEffectType> effectTypes;
    private final List<Integer> effectTypeAmplifiers;
    private final List<CustomEffectType> customEffectTypes;
    private final List<Integer> customEffectTypeAmplifiers;
    private final String name;

    private static final ArrayList<SuperPower> superPowers = new ArrayList<>();

    SuperPower(List<PotionEffectType> effectTypes, List<Integer> effectTypeAmplifiers,
               List<CustomEffectType> customEffectTypes, List<Integer> customEffectTypeAmplifiers,
               String name) {

        this.effectTypes = effectTypes;
        this.customEffectTypes = customEffectTypes;
        this.effectTypeAmplifiers = effectTypeAmplifiers;
        this.customEffectTypeAmplifiers = customEffectTypeAmplifiers;
        this.name = name;
    }

    public static Set<Map.Entry<String, SuperPower>> getPlayerNameSuperPowerEntrySet() {
        return playerSuperPowers.entrySet();
    }

    public static void clearPlayers() {

        for (Map.Entry<String, SuperPower> playerSuperPower : playerSuperPowers.entrySet()) {
            clearPlayerSuperPower(playerSuperPower.getKey());
        }
    }

    public static ArrayList<SuperPower> getSuperPowers() {
        return superPowers;
    }

    public static SuperPower getByName(String name) {

        for (SuperPower superPower : superPowers) {
            if (name.toUpperCase().equals(superPower.getName())) {
                return superPower;
            }
        }
        return null;
    }

    public static SuperPower getRandom() {

        if (superPowers.isEmpty()) {
            return null;
        }

        int superPowerNumber = new Random().nextInt(0, superPowers.size());

        return superPowers.get(superPowerNumber);
    }

    public static boolean isEmpty() {
        return superPowers.isEmpty();
    }

    public static SuperPower getPlayerSuperPower(String playerName) {
        return playerSuperPowers.get(playerName);
    }

    public static SuperPower getPlayerSuperPower(Player player) {
        return playerSuperPowers.get(player.getName());
    }

    public static void clearPlayerSuperPower(String playerName) {

        try {
            SuperPower playerSuperPower = SuperPower.getPlayerSuperPower(playerName);

            for (PotionEffectType potionEffectType : playerSuperPower.getEffectTypes()) {
                Bukkit.getPlayer(playerName).removePotionEffect(potionEffectType);
            }
        } catch (Exception ignored) {}

        playerSuperPowers.remove(playerName);
    }

    public static void clearPlayerSuperPower(Player player) {

        try {
            SuperPower playerSuperPower = SuperPower.getPlayerSuperPower(player);

            for (PotionEffectType potionEffectType : playerSuperPower.getEffectTypes()) {
                player.removePotionEffect(potionEffectType);
            }
        } catch (Exception ignored) {}

        playerSuperPowers.remove(player.getName());
    }

    public static void clearPlayerSuperPowerForced(String playerName) {

        try {

            clearPlayerSuperPower(playerName);

            Player player = Bukkit.getPlayer(playerName);

            for (SuperPower superPower : SuperPower.getSuperPowers()) {

                for (PotionEffectType potionEffectType : superPower.getEffectTypes()) {
                    player.removePotionEffect(potionEffectType);
                }
            }

        } catch (Exception ignored) {}
    }

    public static void clearPlayerSuperPowerForced(Player player) {

        try {

            clearPlayerSuperPower(player);

            for (SuperPower superPower : SuperPower.getSuperPowers()) {

                for (PotionEffectType potionEffectType : superPower.getEffectTypes()) {
                    player.removePotionEffect(potionEffectType);
                }
            }

        } catch (Exception ignored) {}
    }

    public void setToPlayer(Player player) {

        playerSuperPowers.put(player.getName(), this);

        Component header = Component.empty();
        Component text = Component.empty();

        header = header
                .append(Component.text("Superpower:"))
                .append(Component.space())
                .append(Component.text(this.name.toUpperCase())
                        .color(NamedTextColor.GREEN)
                        .decorate(TextDecoration.BOLD));

        text = text
                .append(Component.text("Effects:"));

        for (int i = 0; i < effectTypes.size(); i++) {

            text = text
                    .append(Component.newline())
                    .append(Component.text(effectTypes.get(i).getKey().getKey().toUpperCase())
                    .color(NamedTextColor.GOLD)
                    .decorate(TextDecoration.BOLD))
                    .append(Component.space())
                    .append(Component.text("lvl:"))
                    .append(Component.space())
                    .append(Component.text(effectTypeAmplifiers.get(i) + 1)
                            .decorate(TextDecoration.BOLD));
        }

        for (int i = 0; i < customEffectTypes.size(); i++) {

            text = text
                    .append(Component.newline())
                    .append(Component.text(customEffectTypes.get(i).getName().toUpperCase())
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD))
                    .append(Component.space())
                    .append(Component.text("lvl:"))
                    .append(Component.space())
                    .append(Component.text(customEffectTypeAmplifiers.get(i) + 1)
                            .decorate(TextDecoration.BOLD));
        }

        Common.sendNotification(header, text, player);
    }

    public List<PotionEffectType> getEffectTypes() {
        return this.effectTypes;
    }

    public List<Integer> getEffectTypeAmplifiers() {
        return this.effectTypeAmplifiers;
    }

    public List<CustomEffectType> getCustomEffectTypes() {
        return this.customEffectTypes;
    }

    public List<Integer> getCustomEffectTypeAmplifiers() {
        return this.customEffectTypeAmplifiers;
    }

    public String getName() {
        return this.name;
    }

    public static void deserialize(ConfigurationSection superPowerConfig, String superPowerName) {

        Map<String, Object> serializedEffects = superPowerConfig.getConfigurationSection("effects").getValues(false);

        ArrayList<PotionEffectType> deserializedEffectTypes = new ArrayList<>();
        ArrayList<CustomEffectType> deserializedCustomEffectTypes = new ArrayList<>();
        ArrayList<Integer> deserializedEffectTypeAmplifiers = new ArrayList<>();
        ArrayList<Integer> deserializedCustomEffectTypeAmplifiers = new ArrayList<>();

        for (Map.Entry<String, Object> serializedEffectWithNumber : serializedEffects.entrySet()) {

            Map<String, Object> serializedEffect = superPowerConfig.getConfigurationSection("effects." + serializedEffectWithNumber.getKey()).getValues(false);

            String effectName = (String) serializedEffect.get("name");
            int effectAmplifier = 1;
            try {
                effectAmplifier = (int) serializedEffect.get("level") - 1;
            } catch (Exception ignored) {}

            PotionEffectType minecraftEffectType = PotionEffectType.getByKey(NamespacedKey.minecraft(effectName));
            if (minecraftEffectType == null) {
                minecraftEffectType = PotionEffectType.getByName(effectName.toUpperCase());
            }
            PotionEffectType effectType = PotionEffectType.getByKey(NamespacedKey.fromString(effectName));
            CustomEffectType customEffectType = null;
            try {
                customEffectType = CustomEffectType.valueOf(effectName.toUpperCase());
            } catch (Exception ignored) {}

            if (minecraftEffectType == null && effectType == null && customEffectType == null) {
                Logger.getLogger().warn("Wrong effect name in superpowers config + \"" + effectName + "\"");
                continue;
            }

            if (customEffectType != null) {
                deserializedCustomEffectTypes.add(customEffectType);
                deserializedCustomEffectTypeAmplifiers.add(effectAmplifier);
            }

            else if (minecraftEffectType != null) {
                deserializedEffectTypes.add(minecraftEffectType);
                deserializedEffectTypeAmplifiers.add(effectAmplifier);
            }

            else if (effectType != null) {
                deserializedEffectTypes.add(effectType);
                deserializedEffectTypeAmplifiers.add(effectAmplifier);
            }
        }

        superPowers.add(new SuperPower(deserializedEffectTypes, deserializedEffectTypeAmplifiers,
                deserializedCustomEffectTypes, deserializedCustomEffectTypeAmplifiers,
                superPowerName.toUpperCase()));
    }
}