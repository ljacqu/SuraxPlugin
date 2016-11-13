package ch.jalu.surax.commands;

import ch.jalu.surax.Permission;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Executable command interface.
 */
public interface Command {

    /**
     * Returns the command name.
     *
     * @return the name of the command, without slash
     */
    String getName();

    /**
     * Executes the command.
     *
     * @param sender the sender invoking the command
     * @param arguments the provided arguments
     */
    void execute(CommandSender sender, List<String> arguments);

    /**
     * Returns the permission required to execute the command.
     *
     * @return the required permission, or null if none required
     */
    @Nullable
    default Permission getRequiredPermission() {
        return null;
    }

}
