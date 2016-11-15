package ch.jalu.surax.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Command that can only be run by an in-game player.
 */
public abstract class PlayerCommand implements Command {

    @Override
    public void execute(CommandSender sender, List<String> arguments) {
        if (sender instanceof Player) {
            execute((Player) sender, arguments);
        } else {
            sender.sendMessage("This command can only be run by players!");
        }
    }

    protected abstract void execute(Player player, List<String> arguments);
}
