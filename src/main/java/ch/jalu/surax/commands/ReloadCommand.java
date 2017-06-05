package ch.jalu.surax.commands;

import ch.jalu.injector.Injector;
import ch.jalu.surax.Permission;
import ch.jalu.surax.Reloadable;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;
import java.util.List;

/**
 * Reload command.
 */
public class ReloadCommand implements Command {

    @Inject
    private Injector injector;


    @Override
    public String getName() {
        return "suraxreload";
    }

    @Override
    public void execute(CommandSender sender, List<String> arguments) {
        injector.retrieveAllOfType(Reloadable.class).forEach(Reloadable::reload);
        sender.sendMessage("Reloaded successfully!");
    }

    @Override
    public Permission getRequiredPermission() {
        return Permission.RELOAD_COMMAND;
    }
}
