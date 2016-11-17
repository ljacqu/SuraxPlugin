package ch.jalu.surax.service;

import java.util.HashSet;
import java.util.Set;

/**
 * Keeps track of which players are frozen. Frozen players are only saved in memory,
 * i.e. after a restart no one is frozen.
 */
public class FreezeManager {

    private Set<String> frozenPlayers = new HashSet<>();

    public Set<String> getAllPlayers() {
        return frozenPlayers;
    }

    public boolean isFrozen(String name) {
        return frozenPlayers.contains(name.toLowerCase());
    }

    public void addPlayer(String name) {
        frozenPlayers.add(name.toLowerCase());
    }

    public void removePlayer(String name) {
        frozenPlayers.remove(name.toLowerCase());
    }
}
