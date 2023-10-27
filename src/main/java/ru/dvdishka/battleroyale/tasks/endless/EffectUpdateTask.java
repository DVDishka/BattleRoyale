package ru.dvdishka.battleroyale.tasks.endless;

import ru.dvdishka.battleroyale.classes.SuperPower;
import ru.dvdishka.battleroyale.common.Common;
import ru.dvdishka.battleroyale.common.Logger;
import ru.dvdishka.battleroyale.common.Scheduler;
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

                Player player = Bukkit.getPlayer(playerPower.getKey());
                assert player != null;

                List<PotionEffectType> effectTypes = playerPower.getValue().getEffects();
                List<Integer> amplifiers = playerPower.getValue().getAmplifiers();

                Scheduler.getScheduler().runPlayerTask(Common.plugin, player, (scheduledTask) -> {
                    for (int i = 0; i < effectTypes.size(); i++) {
                        if (!player.hasPotionEffect(effectTypes.get(i)) || player.getPotionEffect(effectTypes.get(i)) != null
                                && player.getPotionEffect(effectTypes.get(i)).getAmplifier() < amplifiers.get(i)) {
                            player.addPotionEffect(new PotionEffect(effectTypes.get(i), 99999999, amplifiers.get(i), false, false, true));
                        }
                    }
                });
            }
        }
    }
}
