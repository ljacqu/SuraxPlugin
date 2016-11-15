package ch.jalu.surax;

import ch.jalu.injector.Injector;
import ch.jalu.injector.InjectorBuilder;
import ch.jalu.surax.config.InvisibilityConfig;
import ch.jalu.surax.domain.DataFolder;
import ch.jalu.surax.listeners.InvisibilityListener;
import ch.jalu.surax.listeners.ServerListener;
import ch.jalu.surax.service.CommandHandler;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Main plugin file.
 */
public class SuraxPlugin extends JavaPlugin {

    private CommandHandler commandHandler;
    private InvisibilityConfig invisibilityConfig;

    @Override
    public void onEnable() {
        Injector injector = new InjectorBuilder()
            .addDefaultHandlers("ch.jalu.surax")
            .create();

        injector.register(SuraxPlugin.class, this);
        injector.register(Logger.class, getLogger());
        injector.register(Server.class, getServer());
        injector.register(PluginManager.class, getServer().getPluginManager());
        injector.provide(DataFolder.class, getDataFolder());

        commandHandler = injector.getSingleton(CommandHandler.class);
        invisibilityConfig = injector.getSingleton(InvisibilityConfig.class);

        registerListeners(injector, ServerListener.class, InvisibilityListener.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commandHandler.handleCommand(sender, label, args);
    }

    @Override
    public void onDisable() {
        invisibilityConfig.save();
    }

    @SafeVarargs
    private final void registerListeners(Injector injector, Class<? extends Listener>... listenerClasses) {
        final PluginManager pluginManager = getServer().getPluginManager();
        Arrays.stream(listenerClasses)
            .map(injector::getSingleton)
            .forEach(l -> pluginManager.registerEvents(l, this));
    }


}
