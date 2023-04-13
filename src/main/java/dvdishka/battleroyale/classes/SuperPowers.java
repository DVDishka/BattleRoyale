package dvdishka.battleroyale.classes;

import dvdishka.battleroyale.common.CommonVariables;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public enum SuperPowers {

    Jumper(List.of(PotionEffectType.JUMP), List.of(2), "Jumper"),
    Sprinter(List.of(PotionEffectType.SPEED), List.of(1), "Sprinter"),
    Miner(List.of(PotionEffectType.FAST_DIGGING), List.of(4), "Miner"),
    FireMan(List.of(PotionEffectType.FIRE_RESISTANCE), List.of(1), "Fireman"),
    AquaMan(List.of(PotionEffectType.WATER_BREATHING, PotionEffectType.DOLPHINS_GRACE), List.of(1, 2), "Aqua-man"),
    Cat(List.of(PotionEffectType.NIGHT_VISION, PotionEffectType.INVISIBILITY), List.of(1, 1), "Cat"),
    Husky(List.of(PotionEffectType.HEALTH_BOOST), List.of(1), "Husky"),
    Feather(List.of(PotionEffectType.SLOW_FALLING), List.of(3), "Feather"),
    Tank(List.of(PotionEffectType.DAMAGE_RESISTANCE, PotionEffectType.SLOW), List.of(1, 0), "Tank"),
    Healer(List.of(PotionEffectType.REGENERATION, PotionEffectType.HUNGER), List.of(1, 0), "Healer"),
    BountyHunter(List.of(PotionEffectType.INCREASE_DAMAGE, PotionEffectType.SLOW), List.of(1, 0), "BountyHunter");

    SuperPowers(List<PotionEffectType> effectType, List<Integer> amplifier, String name) {
        this.effectType = effectType;
        this.amplifier = amplifier;
        this.name = name;
    }

    private final List<PotionEffectType> effectType;
    private final List<Integer> amplifier;
    private final String name;

    public void setToPlayer(Player player) {

        String subTitle = "";
        EntityScheduler playerScheduler = player.getScheduler();
        ArrayList<PotionEffect> potionEffects = new ArrayList<>();

        for (int i = 0; i < effectType.size(); i++) {

            potionEffects.add(new PotionEffect(this.effectType.get(i), 999999999, this.amplifier.get(i), false, false, true));
            subTitle = subTitle.concat(effectType.get(i).getName());
        }

        playerScheduler.run(CommonVariables.plugin, (task) -> {
            player.addPotionEffects(potionEffects);
        }, null);

        String title = subTitle;

        player.sendTitle(ChatColor.LIGHT_PURPLE + name, ChatColor.BLUE + title);
    }

    public String getName() {
        return this.name;
    }
}
