package ch.jalu.surax.listeners;

import ch.jalu.surax.service.AutoSowService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.inject.Inject;

/**
 * Listener for the auto-sow feature.
 */
public class AutoSowListener implements Listener {

    @Inject
    private AutoSowService autoSowService;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        autoSowService.processPlayerMove(event.getPlayer());
    }

}
