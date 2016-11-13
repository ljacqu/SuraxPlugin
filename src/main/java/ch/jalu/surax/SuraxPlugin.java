package ch.jalu.surax;

import ch.jalu.injector.Injector;
import ch.jalu.injector.InjectorBuilder;
import ch.jalu.surax.service.CommandHandler;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Main plugin file.
 */
public class SuraxPlugin extends JavaPlugin {

    private CommandHandler commandHandler;

    @Override
    public void onEnable() {
        Injector injector = new InjectorBuilder()
            .addDefaultHandlers("ch.jalu.surax")
            .create();

        injector.register(SuraxPlugin.class, this);
        injector.register(Logger.class, getLogger());
        injector.register(Server.class, getServer());
        injector.register(PluginManager.class, getServer().getPluginManager());

        commandHandler = injector.getSingleton(CommandHandler.class);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commandHandler.handleCommand(sender, label, args);
    }
}
