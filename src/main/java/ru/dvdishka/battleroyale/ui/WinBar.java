package ru.dvdishka.battleroyale.ui;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import ru.dvdishka.battleroyale.logic.Common;

public class WinBar {

    private static WinBar instance = null;

    private BossBar winBar;

    private WinBar() {}

    public static WinBar getInstance() {

        if (instance == null) {
            instance = new WinBar();
        }
        return instance;
    }

    public void register(String winTeamName, NamedTextColor winTeamColor) {

        this.winBar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID);
        this.winBar.setVisible(true);
        this.winBar.setProgress(1);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Common.players.contains(player.getName())) {
                winBar.addPlayer(player);
            }
        }

        this.winBar.setTitle(ChatColor.LIGHT_PURPLE + "WIN" +
                ChatColor.LIGHT_PURPLE + (ChatColor.BOLD + " | ") + ChatColor.RESET +
                (ChatColor.BOLD + winTeamName) + ChatColor.RESET +
                ChatColor.LIGHT_PURPLE + (ChatColor.BOLD + " | ") + ChatColor.RESET +
                ChatColor.LIGHT_PURPLE + "WIN");
    }

    public void unregister() {
        try {
            this.winBar.removeAll();
            this.winBar.setVisible(false);
        } catch (Exception ignored) {}
    }
}
