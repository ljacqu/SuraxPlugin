package ch.jalu.surax.service;


import ch.jalu.surax.config.InvisibilityConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Updates game properties when a player hides or unhides from others.
 */
public class InvisibilityManager {

    @Inject
    private InvisibilityConfig config;

    @Inject
    private EssentialsHook essentialsHook;

    InvisibilityManager() {
    }

    @PostConstruct
    private void setup() {
        Map<String, Player> onlinePlayers = Bukkit.getOnlinePlayers().stream()
            .collect(Collectors.toMap(p -> p.getName().toLowerCase(), p -> p));
        for (Map.Entry<String, Set<String>> entry : config.getAllBlockedPlayers().entrySet()) {
            Player hiddenPlayer = onlinePlayers.get(entry.getKey());
            if (hiddenPlayer != null) {
                entry.getValue().stream()
                    .map(onlinePlayers::get)
                    .filter(p -> p != null)
                    .forEach(p -> {
                        p.hidePlayer(hiddenPlayer);
                        essentialsHook.processHide(hiddenPlayer.getName(), p.getName());
                    });
            }
        }
    }

    public void processHide(@Nullable Player hider, Collection<String> hidee) {
        if (hider == null) {
            return;
        }
        hidee.stream()
            .peek(h -> essentialsHook.processHide(hider.getName(), h))
            .map(Bukkit::getPlayerExact)
            .filter(p -> p != null)
            .forEach(p -> p.hidePlayer(hider));
    }

    public void processUnhide(@Nullable Player hider, Collection<String> hidee) {
        if (hider == null) {
            return;
        }
        hidee.stream()
            .peek(h -> essentialsHook.processUnhide(hider.getName(), h))
            .map(Bukkit::getPlayerExact)
            .filter(p -> p != null)
            .forEach(p -> p.showPlayer(hider));
    }

    public void setHideEffectsOnJoin(Player player) {
        final String playerName = player.getName();
        final Map<String, Set<String>> allBlockedPlayers = config.getAllBlockedPlayers();
        // Hide player from the players he supplied
        Set<String> playersHiddenFrom = allBlockedPlayers.get(playerName);
        if (isNotEmpty(playersHiddenFrom)) {
            processHide(player, playersHiddenFrom);
        }
        // player might be the hidee also, so hide other players for him when he joins
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Set<String> onlinePlayerHiddenFrom = allBlockedPlayers.get(onlinePlayer.getName());
            if (onlinePlayerHiddenFrom != null && onlinePlayerHiddenFrom.contains(playerName)) {
                player.hidePlayer(onlinePlayer);
            }
        }
    }

    private static boolean isNotEmpty(Collection<String> coll) {
        return coll != null && !coll.isEmpty();
    }
}
