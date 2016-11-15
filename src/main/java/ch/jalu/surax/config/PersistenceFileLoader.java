package ch.jalu.surax.config;

import ch.jalu.injector.Injector;
import ch.jalu.surax.domain.DataFolder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Loads the file with persisted data.
 */
public class PersistenceFileLoader {

    @Inject
    @DataFolder
    private File dataFolder;
    @Inject
    private Logger logger;
    @Inject
    private Injector injector;

    private File configFile;
    private FileConfiguration configuration;

    PersistenceFileLoader() {
    }

    @PostConstruct
    private void loadFile() throws IOException {
        configFile = new File(dataFolder, "data.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();
        }

        configuration = YamlConfiguration.loadConfiguration(configFile);
    }

    @Nullable
    public <T> T getEntry(String path, Class<T> type) {
        Object obj = configuration.get(path);
        if (type.isInstance(obj)) {
            return type.cast(obj);
        }
        return null;
    }

    public void setSection(String path, Object object) {
        configuration.set(path, object);
    }

    public void save() {
        injector.retrieveAllOfType(PrePersist.class).forEach(PrePersist::prePersist);

        try {
            configuration.save(configFile);
        } catch (IOException e) {
            logger.warning("Could not save config: got " + e.getMessage());
        }
    }
}
