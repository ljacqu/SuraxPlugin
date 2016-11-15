package ch.jalu.surax.listeners;

import ch.jalu.surax.config.PvpStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import javax.inject.Inject;

/**
 * Listener for PVP event.
 */
public class PvpListener implements Listener {

    @Inject
    private PvpStorage pvpStorage;

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            if (!pvpStorage.doesPlayerHavePvp(event.getDamager().getName())
                || !pvpStorage.doesPlayerHavePvp(event.getEntity().getName())) {
                event.getDamager().sendMessage("PVP is disabled for you or the target");
                event.setCancelled(true);
            }
        }
    }
}
