package ch.jalu.surax;

import ch.jalu.surax.service.EssentialsHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import javax.inject.Inject;

/**
 * Listener for plugin events.
 */
public class ServerListener implements Listener {

    @Inject
    private EssentialsHook essentialsHook;

    ServerListener() {
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPluginEnable(PluginEnableEvent event) {
        if ("Essentials".equals(event.getPlugin().getName())) {
            essentialsHook.hook();
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPluginDisable(PluginDisableEvent event) {
        if ("Essentials".equals(event.getPlugin().getName())) {
            essentialsHook.unhook();
        }
    }
}
