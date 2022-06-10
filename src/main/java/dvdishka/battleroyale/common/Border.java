package dvdishka.battleroyale.common;

import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;

public class Border {

    public static void setSize(int size, int time) {
        WorldBorder worldBorder = Bukkit.getWorld("world").getWorldBorder();
        worldBorder.setCenter(0, 0);
        worldBorder.setSize(size, time);
    }
}
