package ch.jalu.surax.listeners;

import ch.jalu.surax.service.FreezeManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import javax.inject.Inject;

/**
 * Cancels events for frozen players.
 */
public class FrozenPlayerListener implements Listener {

    @Inject
    private FreezeManager freezeManager;

    FrozenPlayerListener() {
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (freezeManager.isFrozen(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (freezeManager.isFrozen(event.getPlayer().getName())) {
            event.getPlayer().sendMessage(ChatColor.RED + "You are frozen!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (freezeManager.isFrozen(event.getPlayer().getName()) && !event.getMessage().startsWith("/unfreeze")) {
            event.getPlayer().sendMessage(ChatColor.RED + "You are frozen!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && freezeManager.isFrozen(event.getEntity().getName())) {
            event.setCancelled(true);
        }
    }
}
