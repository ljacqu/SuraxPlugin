package ch.jalu.surax.listeners;

import ch.jalu.surax.service.WorldGuardHook;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Listener for WorldGuard features.
 */
public class WorldGuardListener implements Listener {

    @Inject
    private WorldGuardHook worldGuardHook;
    @Inject
    private Logger logger;

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Location destination = event.getTo();
        if (worldGuardHook.isInProtectedRegion(destination)) {
            event.setCancelled(true);
            logger.info("Player '" + event.getPlayer().getName() + "' tried to TP into protected WG region");
        }
    }
}
