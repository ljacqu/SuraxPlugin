package ch.jalu.surax.service;


import ch.jalu.surax.config.InvisibilityConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        CachingPlayerGetter playerGetter = new CachingPlayerGetter();
        for (Map.Entry<String, Set<String>> entry : config.getAllBlockedPlayers().entrySet()) {
            Player hiddenPlayer = playerGetter.get(entry.getKey());
            if (hiddenPlayer != null) {
                entry.getValue().stream()
                    .map(playerGetter::get)
                    .filter(p -> p != null)
                    .forEach(p -> {
                        p.hidePlayer(hiddenPlayer);
                        essentialsHook.processHide(hiddenPlayer.getName(), p.getName());
                    });
            }
        }
    }

    public void processHide(Player hider, String hidee) {
        Player hideePlayer = Bukkit.getPlayerExact(hidee);
        if (hideePlayer != null) {
            hideePlayer.hidePlayer(hider);
        }
        essentialsHook.processHide(hider.getName(), hidee);
    }

    public void processUnhide(Player hider, String hidee) {
        Player hideePlayer = Bukkit.getPlayerExact(hidee);
        if (hideePlayer != null) {
            hideePlayer.showPlayer(hider);
        }
        essentialsHook.processUnhide(hider.getName(), hidee);
    }

    private static final class CachingPlayerGetter {

        private final Map<String, Player> players = new HashMap<>();

        @Nullable
        Player get(String name) {
            Player p = players.get(name.toLowerCase());
            if (p == null) {
                p = Bukkit.getPlayerExact(name);
                if (p != null) {
                    players.put(name, p);
                }
            }
            return p;
        }
    }

}
