package ch.jalu.surax.listeners;

import ch.jalu.injector.Injector;
import ch.jalu.surax.service.AbstractPluginHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import javax.inject.Inject;
import java.util.function.Consumer;

/**
 * Listener for plugin events.
 */
public class ServerListener implements Listener {

    @Inject
    private Injector injector;

    ServerListener() {
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPluginEnable(PluginEnableEvent event) {
        applyToMatchingHooks(event.getPlugin().getName(), AbstractPluginHook::hook);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPluginDisable(PluginDisableEvent event) {
        applyToMatchingHooks(event.getPlugin().getName(), AbstractPluginHook::unhook);
    }

    private void applyToMatchingHooks(String pluginName, Consumer<AbstractPluginHook> action) {
        injector.retrieveAllOfType(AbstractPluginHook.class)
            .stream()
            .filter(hook -> pluginName.equals(hook.getPluginName()))
            .forEach(action);
    }
}
