package ch.jalu.surax.service;

import ch.jalu.injector.Injector;
import ch.jalu.surax.Permission;
import ch.jalu.surax.commands.Command;
import ch.jalu.surax.commands.FreezeCommand;
import ch.jalu.surax.commands.HideCommand;
import ch.jalu.surax.commands.HideMeCommand;
import ch.jalu.surax.commands.NearHomeCommand;
import ch.jalu.surax.commands.PvpCommand;
import ch.jalu.surax.commands.UnfreezeCommand;
import ch.jalu.surax.commands.UnhideMeCommand;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles incoming commands.
 */
public class CommandHandler {

    public static final List<Class<? extends Command>> COMMAND_CLASSES = ImmutableList.of(
        NearHomeCommand.class, HideCommand.class, HideMeCommand.class, UnhideMeCommand.class, PvpCommand.class,
        FreezeCommand.class, UnfreezeCommand.class);
    private Map<String, Command> commands = new HashMap<>();

    @Inject
    CommandHandler(Injector injector) {
        initCommandMap(injector);
    }

    public boolean handleCommand(CommandSender sender, String label, String[] args) {
        Command command = commands.get(label.toLowerCase());
        if (command == null) {
            sender.sendMessage("Could not find command for label '" + label + "'");
            return false;
        }

        runCommand(command, sender, args);
        return true;
    }

    private void runCommand(Command command, CommandSender sender, String[] args) {
        Permission requiredPermission = command.getRequiredPermission();
        if (requiredPermission != null && !requiredPermission.allows(sender)) {
            sender.sendMessage("You don't have permission for this command!");
        } else {
            command.execute(sender, Arrays.asList(args));
        }
    }

    private void initCommandMap(Injector injector) {
        COMMAND_CLASSES.stream()
            .map(injector::getSingleton)
            .forEach(cmd -> commands.put(cmd.getName(), cmd));
    }
}
