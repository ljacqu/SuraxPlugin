package ch.jalu.surax.listeners;

import ch.jalu.surax.config.InvisibilityConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;
import java.util.Set;

/**
 * Listener for events around invisibility.
 */
public class InvisibilityListener implements Listener {

    @Inject
    private InvisibilityConfig config;

    InvisibilityListener() {
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Set<String> playersHiddenFrom = config.getBlockedPlayers(event.getPlayer().getName());
        if (!playersHiddenFrom.isEmpty()) {
            event.getRecipients()
                .removeIf(p -> playersHiddenFrom.contains(p.getName().toLowerCase()));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Set<String> playersHiddenFrom = config.getBlockedPlayers(event.getPlayer().getName());
        if (!playersHiddenFrom.isEmpty()) {
            final String quitMessage = event.getQuitMessage();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!playersHiddenFrom.contains(player.getName().toLowerCase())) {
                    player.sendMessage(quitMessage);
                }
            }
            // We sent the quit message manually -- so set it to null
            event.setQuitMessage(null);
        }
    }
}
