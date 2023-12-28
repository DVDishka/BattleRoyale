package ru.dvdishka.battleroyale.handlers.superpowerhandler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import ru.dvdishka.battleroyale.logic.classes.superpower.CustomEffectType;
import ru.dvdishka.battleroyale.logic.classes.superpower.SuperPower;

public class NoFallDamageHandler implements Listener {

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        SuperPower superPower = SuperPower.getPlayerSuperPower(player);

        if (superPower == null || !superPower.getCustomEffectTypes().contains(CustomEffectType.NO_FALL_DAMAGE)) {
            return;
        }

        event.setCancelled(true);
    }
}
