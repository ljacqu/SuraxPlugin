package ch.jalu.surax.config;

import ch.jalu.surax.domain.DataFolder;
import org.bukkit.configuration.MemorySection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Handles the configuration of which players should be hidden for whom.
 */
public class InvisibilityConfig implements PrePersist {

    @Inject
    @DataFolder
    private File dataFolder;
    @Inject
    private Logger logger;
    @Inject
    private PersistenceFileLoader fileLoader;

    private File configFile;
    private Map<String, Set<String>> invisibilityRules = new HashMap<>();

    InvisibilityConfig() {
    }

    @PostConstruct
    private void loadInvisibilityRules() {
        MemorySection rules = fileLoader.getEntry("hidden", MemorySection.class);
        if (rules != null) {
            for (String key : rules.getKeys(false)) {
                Object entry = rules.get(key);
                if (entry instanceof List<?>) {
                    Set<String> ignoredPlayers = ((List<?>) entry).stream()
                        .filter(o -> o instanceof String)
                        .map(o -> (String) o).collect(Collectors.toSet());
                    invisibilityRules.put(key, ignoredPlayers);
                }
            }
        }
        logger.info("Found invisibility rules for " + invisibilityRules.size() + " players");
    }

    public Set<String> getBlockedPlayers(String name) {
        Set<String> ignoredPlayers = invisibilityRules.get(name.toLowerCase());
        return ignoredPlayers == null
            ? Collections.emptySet()
            : ignoredPlayers;
    }

    public void addBlockedPlayer(String ignorer, List<String> ignoree) {
        Set<String> ignoredPlayers = invisibilityRules.get(ignorer.toLowerCase());
        if (ignoredPlayers == null) {
            ignoredPlayers = new HashSet<>();
            invisibilityRules.put(ignorer.toLowerCase(), ignoredPlayers);
        }
        ignoredPlayers.addAll(ignoree.stream().map(String::toLowerCase).collect(Collectors.toList()));
    }

    public void removeBlockedPlayer(String ignorer, List<String> ignoree) {
        Set<String> ignoredPlayers = invisibilityRules.get(ignorer.toLowerCase());
        if (ignoredPlayers != null) {
            ignoredPlayers.removeAll(ignoree.stream().map(String::toLowerCase).collect(Collectors.toList()));
        }
    }

    public Map<String, Set<String>> getAllBlockedPlayers() {
        return invisibilityRules;
    }

    @Override
    public void prePersist() {
        Map<String, List<String>> exportableRules = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : invisibilityRules.entrySet()) {
            exportableRules.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        fileLoader.setSection("rules", exportableRules);
    }
}
