package ch.jalu.surax.service;

import ch.jalu.injector.Injector;
import ch.jalu.surax.Permission;
import ch.jalu.surax.commands.BakeAllCommand;
import ch.jalu.surax.commands.Command;
import ch.jalu.surax.commands.FixupCommand;
import ch.jalu.surax.commands.FreezeCommand;
import ch.jalu.surax.commands.GlowCommand;
import ch.jalu.surax.commands.HideCommand;
import ch.jalu.surax.commands.HideMeCommand;
import ch.jalu.surax.commands.NearHomeCommand;
import ch.jalu.surax.commands.PvpCommand;
import ch.jalu.surax.commands.ReopCommand;
import ch.jalu.surax.commands.TDeopCommand;
import ch.jalu.surax.commands.UnfreezeCommand;
import ch.jalu.surax.commands.UnhideCommand;
import ch.jalu.surax.commands.UnhideMeCommand;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles incoming commands.
 */
public class CommandHandler {

    public static final List<Class<? extends Command>> COMMAND_CLASSES = Arrays.asList(
        NearHomeCommand.class, HideCommand.class,   UnhideCommand.class,   HideMeCommand.class, UnhideMeCommand.class,
        PvpCommand.class,      FreezeCommand.class, UnfreezeCommand.class, TDeopCommand.class,  ReopCommand.class,
        BakeAllCommand.class,  FixupCommand.class,  GlowCommand.class);

    private final Map<String, Command> commands;

    @Inject
    CommandHandler(Injector injector) {
        commands = initCommandMap(injector);
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

    private Map<String, Command> initCommandMap(Injector injector) {
        return COMMAND_CLASSES.stream()
            .map(injector::getSingleton)
            .collect(Collectors.toMap(Command::getName, cmd -> cmd));
    }
}
