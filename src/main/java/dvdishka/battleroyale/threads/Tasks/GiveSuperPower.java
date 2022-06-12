package dvdishka.battleroyale.threads.Tasks;

import com.destroystokyo.paper.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Random;

public class GiveSuperPower {

    public static void Execute() {

        Random random = new Random();
        HashSet<Integer> usedID = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            int id = random.nextInt(0, SuperPowers.values().length);
            if (usedID.contains(id) && usedID.size() < SuperPowers.values().length) {
                while (usedID.contains(id)) {
                    id = random.nextInt(0, SuperPowers.values().length);
                }
            }
            if (usedID.size() >= SuperPowers.values().length) {
                usedID.clear();
            }
            usedID.add(id);
            SuperPowers superPower = SuperPowers.values()[id];
            superPower.setToPlayer(player);
            player.sendTitle(Title.builder().title(ChatColor.GOLD + "You are " + superPower.getName()).build());
        }
    }
}
