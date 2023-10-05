package ru.dvdishka.battleroyale.classes;

import ru.dvdishka.battleroyale.common.CommonVariables;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

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
    Healer(List.of(PotionEffectType.REGENERATION, PotionEffectType.DARKNESS), List.of(3, 0), "Healer"),
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

        String subTitle = "";

        for (int i = 0; i < effectType.size(); i++) {

            subTitle = subTitle.concat(effectType.get(i).getName());
        }

        String title = subTitle;

        CommonVariables.playersPower.put(player.getName(), this);
        player.sendTitle(ChatColor.LIGHT_PURPLE + name, ChatColor.BLUE + title);
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
