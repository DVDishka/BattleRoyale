package ru.dvdishka.battleroyale.logic.classes.superpower;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import ru.dvdishka.battleroyale.logic.Common;

import java.util.ArrayList;
import java.util.List;

public enum SuperPower {

    Runner(List.of(PotionEffectType.JUMP, PotionEffectType.SPEED), List.of(1, 1),
            List.of(CustomEffectType.NO_FALL_DAMAGE), List.of(0)
            , "Runner"),

    Miner(List.of(PotionEffectType.FAST_DIGGING), List.of(4), "Miner"),

    AquaMan(List.of(PotionEffectType.FIRE_RESISTANCE, PotionEffectType.WATER_BREATHING, PotionEffectType.DOLPHINS_GRACE), List.of(0, 0, 3), "Aqua-man"),

    Cat(List.of(PotionEffectType.NIGHT_VISION, PotionEffectType.INVISIBILITY, PotionEffectType.LUCK), List.of(0, 0, 4), "Cat"),

    Husky(List.of(PotionEffectType.HEALTH_BOOST), List.of(2), "Husky"),

    Tank(List.of(PotionEffectType.DAMAGE_RESISTANCE, PotionEffectType.SLOW), List.of(0, 0), "Tank"),

    Healer(List.of(PotionEffectType.REGENERATION), List.of(0, 0), "Healer"),

    BountyHunter(List.of(PotionEffectType.INCREASE_DAMAGE, PotionEffectType.GLOWING), List.of(0, 0), "Bounty Hunter");

    SuperPower(List<PotionEffectType> effectTypes, List<Integer> effectTypeAmplifiers, String name) {

        this.effectTypes = effectTypes;
        this.customEffectTypes = new ArrayList<>();
        this.effectTypeAmplifiers = effectTypeAmplifiers;
        this.customEffectTypeAmplifiers = new ArrayList<>();
        this.name = name;
    }

    SuperPower(List<PotionEffectType> effectTypes, List<Integer> effectTypeAmplifiers,
               List<CustomEffectType> customEffectTypes, List<Integer> customEffectTypeAmplifiers,
               String name) {

        this.effectTypes = effectTypes;
        this.customEffectTypes = customEffectTypes;
        this.effectTypeAmplifiers = effectTypeAmplifiers;
        this.customEffectTypeAmplifiers = customEffectTypeAmplifiers;
        this.name = name;
    }

    private final List<PotionEffectType> effectTypes;
    private final List<Integer> effectTypeAmplifiers;
    private final List<CustomEffectType> customEffectTypes;
    private final List<Integer> customEffectTypeAmplifiers;
    private final String name;

    public static SuperPower getPlayerSuperPower(String playerName) {
        return Common.playersPower.get(playerName);
    }

    public static SuperPower getPlayerSuperPower(Player player) {
        return Common.playersPower.get(player.getName());
    }

    public void setToPlayer(Player player) {

        Common.playersPower.put(player.getName(), this);

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

        for (int i = 0; i < customEffectTypeAmplifiers.size(); i++) {

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
}
