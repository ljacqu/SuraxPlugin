package ch.jalu.surax.listeners;

import ch.jalu.surax.config.InvisibilityConfig;
import ch.jalu.surax.service.InvisibilityManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.TabCompleteEvent;

import javax.inject.Inject;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Listener for events around invisibility.
 */
public class InvisibilityListener implements Listener {

    @Inject
    private InvisibilityConfig config;

    @Inject
    private InvisibilityManager invisibilityManager;

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
        sendMessageToNonHiddenPlayers(event::getPlayer, event::getQuitMessage, event::setQuitMessage);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        sendMessageToNonHiddenPlayers(event::getPlayer, event::getJoinMessage, event::setJoinMessage);
        invisibilityManager.setHideEffectsOnJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        sendMessageToNonHiddenPlayers(event::getEntity, event::getDeathMessage, event::setDeathMessage);
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        final String originator = event.getSender().getName();
        event.getCompletions()
            .removeIf(r -> config.getBlockedPlayers(r).contains(originator));
    }

    private void sendMessageToNonHiddenPlayers(Supplier<Player> playerGetter,
                                               Supplier<String> messageGetter,
                                               Consumer<String> messageSetter) {
        Set<String> playersHiddenFrom = config.getBlockedPlayers(playerGetter.get().getName());
        if (!playersHiddenFrom.isEmpty()) {
            final String joinMessage = messageGetter.get();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!playersHiddenFrom.contains(player.getName().toLowerCase())) {
                    player.sendMessage(joinMessage);
                }
            }
            // We sent the quit message manually -- so set it to null
            messageSetter.accept(null);
        }
    }


}
