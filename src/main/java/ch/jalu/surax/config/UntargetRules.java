package ch.jalu.surax.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.EntityType;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ch.jalu.surax.util.EnumUtil.toEnum;

/**
 * Entity untargeting rules.
 */
public class UntargetRules implements PrePersist {

    private Multimap<String, EntityType> untargetRules = HashMultimap.create();

    @Inject
    private PersistenceFileLoader fileLoader;
    @Inject
    private Logger logger;

    public boolean shouldSuppress(String player, EntityType entityType) {
        return untargetRules.get(player).contains(entityType);
    }

    @PostConstruct
    private void loadEntries() {
        MemorySection rules = fileLoader.getEntry("mobTargetSuppression", MemorySection.class);
        if (rules != null) {
            for (String key : rules.getKeys(false)) {
                Object entry = rules.get(key);
                if (entry instanceof List<?>) {
                    Set<EntityType> suppressedEntities = ((List<?>) entry).stream()
                        .filter(o -> o instanceof String)
                        .map(o -> toEnum(EntityType.class, (String) o))
                        .collect(Collectors.toSet());
                    untargetRules.putAll(key, suppressedEntities);
                }
            }
        }
        logger.info("Found untarget rules for " + untargetRules.keySet().size() + " players");
    }

    @Override
    public void prePersist() {
        Map<String, List<String>> plainEntries = new HashMap<>();
        for (Map.Entry<String, Collection<EntityType>> entry : untargetRules.asMap().entrySet()) {
            plainEntries.put(entry.getKey(),
                entry.getValue().stream().map(EntityType::name).collect(Collectors.toList()));
        }
        fileLoader.setSection("mobTargetSuppression", plainEntries);
    }
}
