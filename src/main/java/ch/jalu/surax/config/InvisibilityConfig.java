package ch.jalu.surax.config;

import ch.jalu.surax.domain.DataFolder;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
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
public class InvisibilityConfig {

    @Inject
    @DataFolder
    private File dataFolder;
    @Inject
    private Logger logger;

    private File configFile;
    private Map<String, Set<String>> invisibilityRules = new HashMap<>();

    InvisibilityConfig() {
    }

    @PostConstruct
    private void loadInvisibilityRules() throws IOException {
        configFile = new File(dataFolder, "invisible_rules.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();
        }

        FileConfiguration conf = YamlConfiguration.loadConfiguration(configFile);
        Object rules0 = conf.get("rules");
        if (rules0 instanceof MemorySection) {
            MemorySection rules = (MemorySection) rules0;
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

    public void addBlockedPlayer(String ignorer, String ignoree) {
        Set<String> ignoredPlayers = invisibilityRules.get(ignorer.toLowerCase());
        if (ignoredPlayers == null) {
            invisibilityRules.put(ignorer.toLowerCase(), newHashSet(ignoree.toLowerCase()));
        } else {
            ignoredPlayers.add(ignoree.toLowerCase());
        }
    }

    public void removeBlockedPlayer(String ignorer, String ignoree) {
        Set<String> ignoredPlayers = invisibilityRules.get(ignorer.toLowerCase());
        if (ignoredPlayers != null) {
            ignoredPlayers.remove(ignoree.toLowerCase());
        }
    }

    public void save() {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(configFile);
        Map<String, List<String>> exportableRules = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : invisibilityRules.entrySet()) {
            exportableRules.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        configuration.set("rules", exportableRules);
        try {
            configuration.save(configFile);
        } catch (IOException e) {
            logger.warning("Could not save config: got " + e.getMessage());
        }
    }

    private static Set<String> newHashSet(String name) {
        Set<String> set = new HashSet<>();
        set.add(name);
        return set;
    }

}
