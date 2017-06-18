package ch.jalu.surax.service;

import ch.jalu.injector.Injector;
import ch.jalu.surax.SuraxPlugin;
import ch.jalu.surax.listeners.ChestShopListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import javax.inject.Inject;

/**
 * Hooks into ChestShop.
 */
public class ChestShopHook extends AbstractPluginHook {

    @Inject
    private PluginManager pluginManager;
    @Inject
    private SuraxPlugin suraxPlugin;
    @Inject
    private Injector injector;

    private boolean scheduledListener = false;

    @Override
    protected String getPluginClass() {
        return "com.Acrobot.ChestShop.ChestShop";
    }

    @Override
    public String getPluginName() {
        return "ChestShop";
    }

    @Override
    protected void acceptPlugin(Plugin plugin) {
        if (!scheduledListener) {
            pluginManager.registerEvents(injector.getSingleton(ChestShopListener.class), suraxPlugin);
            scheduledListener = true;
        }
    }

    @Override
    public void unhook() {
    }
}
