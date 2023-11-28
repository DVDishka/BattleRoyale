package ru.dvdishka.battleroyale.logic.classes.superpower;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import ru.dvdishka.battleroyale.logic.Common;

import java.util.List;

public enum SuperPower {

    Jumper(List.of(PotionEffectType.JUMP, PotionEffectType.SLOW_FALLING), List.of(2, 0), "Jumper"),
    Sprinter(List.of(PotionEffectType.SPEED, PotionEffectType.SATURATION), List.of(1, 0), "Sprinter"),
    Miner(List.of(PotionEffectType.FAST_DIGGING), List.of(4), "Miner"),
    FireMan(List.of(PotionEffectType.FIRE_RESISTANCE), List.of(0), "Fireman"),
    AquaMan(List.of(PotionEffectType.WATER_BREATHING, PotionEffectType.DOLPHINS_GRACE), List.of(0, 3), "Aqua-man"),
    Cat(List.of(PotionEffectType.NIGHT_VISION, PotionEffectType.INVISIBILITY, PotionEffectType.LUCK), List.of(0, 0, 4), "Cat"),
    Husky(List.of(PotionEffectType.HEALTH_BOOST), List.of(2), "Husky"),
    Tank(List.of(PotionEffectType.DAMAGE_RESISTANCE, PotionEffectType.SLOW), List.of(1, 0), "Tank"),
    Healer(List.of(PotionEffectType.REGENERATION), List.of(2, 0), "Healer"),
    BountyHunter(List.of(PotionEffectType.INCREASE_DAMAGE, PotionEffectType.GLOWING), List.of(1, 0), "BountyHunter");

    SuperPower(List<PotionEffectType> effectType, List<Integer> amplifier, String name) {
        this.effectType = effectType;
        this.amplifier = amplifier;
        this.name = name;
    }

    private final List<PotionEffectType> effectType;
    private final List<Integer> amplifier;
    private final String name;

    public void setToPlayer(Player player) {

        Common.playersPower.put(player.getName(), this);

        Component message = Component.empty();

        message = message
                .append(Component.newline())
                .append(Component.text("-".repeat(26))
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD))
                .append(Component.newline());

        message = message
                .append(Component.text("Your Superpower:"))
                .append(Component.space())
                .append(Component.text(this.name.toUpperCase())
                        .color(NamedTextColor.GREEN)
                        .decorate(TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.text("-".repeat(27))
                        .color(NamedTextColor.YELLOW))
                .append(Component.newline())
                .append(Component.text("Effects:"))
                .append(Component.newline());

        for (int i = 0; i < effectType.size(); i++) {

            message = message
                    .append(Component.text(effectType.get(i).getKey().getKey().toUpperCase())
                    .color(NamedTextColor.GOLD)
                    .decorate(TextDecoration.BOLD))
                    .append(Component.space())
                    .append(Component.text("lvl:"))
                    .append(Component.space())
                    .append(Component.text(amplifier.get(i) + 1)
                            .decorate(TextDecoration.BOLD))
                    .append(Component.newline());
        }

        message = message
                .append(Component.text("-".repeat(26))
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD))
                .append(Component.newline());

        player.sendMessage(message);
        Common.notificationSound(player);
    }

    public List<PotionEffectType> getEffects() {
        return this.effectType;
    }

    public List<Integer> getAmplifiers() {
        return this.amplifier;
    }

    public String getName() {
        return this.name;
    }
}
