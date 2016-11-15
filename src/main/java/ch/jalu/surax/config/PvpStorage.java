package ch.jalu.surax.config;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manages players with disabled PVP.
 */
public class PvpStorage implements PrePersist {

    private Set<String> disabledPvpPlayers;

    @Inject
    private PersistenceFileLoader fileLoader;

    PvpStorage() {
    }

    @PostConstruct
    private void loadData() {
        List<?> pvpOffPlayers = fileLoader.getEntry("pvpOff", List.class);
        if (pvpOffPlayers != null) {
            disabledPvpPlayers = pvpOffPlayers.stream()
                .filter(e -> e instanceof String)
                .map(e -> (String) e)
                .collect(Collectors.toSet());
        } else {
            disabledPvpPlayers = new HashSet<>();
        }
    }

    public boolean doesPlayerHavePvp(String name) {
        return !disabledPvpPlayers.contains(name.toLowerCase());
    }

    public void addDisabledPvpPlayer(String name) {
        disabledPvpPlayers.add(name.toLowerCase());
    }

    public void removeDisabledPvpPlayer(String name) {
        disabledPvpPlayers.remove(name.toLowerCase());
    }

    @Override
    public void prePersist() {
        List<String> list = new ArrayList<>(disabledPvpPlayers);
        fileLoader.setSection("pvpOff", list);
    }
}
