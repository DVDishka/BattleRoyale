package dvdishka.battleroyale.tasks.endless;

import dvdishka.battleroyale.classes.SuperPower;
import dvdishka.battleroyale.common.CommonVariables;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;

public class EffectUpdateTask implements Runnable {

    @Override
    public void run() {

        for (Map.Entry<String, SuperPower> playerPower : CommonVariables.playersPower.entrySet()) {
            if (Bukkit.getOfflinePlayer(playerPower.getKey()).isOnline()) {

                Player player = Bukkit.getPlayer(playerPower.getKey());
                assert player != null;
                EntityScheduler playerScheduler = player.getScheduler();

                List<PotionEffectType> effectTypes = playerPower.getValue().getEffects();
                List<Integer> amplifiers = playerPower.getValue().getAmplifiers();

                playerScheduler.run(CommonVariables.plugin, (task) -> {
                    for (int i = 0; i < effectTypes.size(); i++) {
                        player.addPotionEffect(new PotionEffect(effectTypes.get(i), 240, amplifiers.get(i), false, false, true));
                    }
                }, null);
            }
        }
    }
}
