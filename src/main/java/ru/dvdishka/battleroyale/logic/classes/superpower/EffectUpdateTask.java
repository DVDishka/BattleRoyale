package ru.dvdishka.battleroyale.logic.classes.superpower;

import ru.dvdishka.battleroyale.logic.Common;
import ru.dvdishka.battleroyale.logic.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;

public class EffectUpdateTask implements Runnable {

    @Override
    public void run() {

        for (Map.Entry<String, SuperPower> playerPower : Common.playersPower.entrySet()) {
            if (Bukkit.getOfflinePlayer(playerPower.getKey()).isOnline()) {

                try {
                    Player player = Bukkit.getPlayer(playerPower.getKey());

                    List<PotionEffectType> effectTypes = playerPower.getValue().getEffectTypes();
                    List<Integer> amplifiers = playerPower.getValue().getEffectTypeAmplifiers();

                    Scheduler.getScheduler().runPlayerTask(Common.plugin, player, (scheduledTask) -> {
                        for (int i = 0; i < effectTypes.size(); i++) {
                            if (!player.hasPotionEffect(effectTypes.get(i)) || player.getPotionEffect(effectTypes.get(i)) != null
                                    && player.getPotionEffect(effectTypes.get(i)).getAmplifier() < amplifiers.get(i)) {
                                player.addPotionEffect(new PotionEffect(effectTypes.get(i), 99999999, amplifiers.get(i), false, false, true));
                            }
                        }
                    });
                } catch (Exception ignored) {}
            }
        }
    }
}
