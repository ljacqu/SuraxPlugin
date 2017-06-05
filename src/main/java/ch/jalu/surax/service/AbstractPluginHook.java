package ch.jalu.surax.service;

import ch.jalu.surax.Reloadable;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Hooks into another plugin which may or may not be present.
 */
public abstract class AbstractPluginHook implements Reloadable {

    private Plugin plugin;

    @Inject
    private Logger logger;
    @Inject
    private PluginManager pluginManager;

    @PostConstruct
    public void hook() {
        final Class<?> pluginClass = getClassOrNull(getPluginClass());
        final String pluginName = getPluginName();
        if (pluginClass == null) {
            logger.info("Plugin '" + pluginName + "' not loaded: cannot hook");
            return;
        }

        final Plugin plugin = pluginManager.getPlugin(getPluginName());
        if (pluginClass.isInstance(plugin)) {
            acceptPlugin(plugin);
            logger.info("Found " + pluginName + " " + plugin.getDescription().getVersion());
        } else if (plugin != null) {
            logger.warning("Plugin '" + pluginName + "' is of unexpected type '" + plugin.getClass() + "'");
        } else {
            logger.info("Could not hook into '" + pluginName + "' - unavailable");
        }
    }

    /**
     * @return fully qualified class name of the plugin
     */
    protected abstract String getPluginClass();

    /**
     * @return the plugin name as defined in its plugin.yml file
     */
    public abstract String getPluginName();

    protected abstract void acceptPlugin(Plugin plugin);

    public abstract void unhook();

    @Override
    public void reload() {
        unhook();
        hook();
    }

    @Nullable
    private static Class<?> getClassOrNull(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
