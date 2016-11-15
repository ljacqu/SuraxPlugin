package ch.jalu.surax.commands;

import ch.jalu.surax.Permission;
import ch.jalu.surax.config.InvisibilityConfig;
import ch.jalu.surax.service.InvisibilityManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

/**
 * Implementation of the {@code /hide} command.
 */
public class HideCommand implements Command {

    private static final String ADD = "add";
    private static final String REMOVE = "remove";

    @Inject
    private InvisibilityConfig config;
    @Inject
    private InvisibilityManager invisibilityManager;

    @Override
    public String getName() {
        return "hide";
    }

    @Override
    public void execute(CommandSender sender, List<String> arguments) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to run this command");
        } else if (arguments.isEmpty()) {
            sender.sendMessage("You are hidden from: " + config.getBlockedPlayers(sender.getName()));
        } else if (!areArgumentsValid(arguments)) {
            sender.sendMessage("Usage: /hide add <player> or /hide remove <player>");
        } else {
            if (ADD.equalsIgnoreCase(arguments.get(0))) {
                config.addBlockedPlayer(sender.getName(), arguments.get(1));
                invisibilityManager.processHide((Player) sender, arguments.get(1));
                sender.sendMessage("You are now hidden from player " + arguments.get(1));
            } else { // REMOVE.equalsIgnoreCase
                config.removeBlockedPlayer(sender.getName(), arguments.get(1));
                invisibilityManager.processUnhide((Player) sender, arguments.get(1));
                sender.sendMessage("Player " + arguments.get(1) + " will now see you again");
            }
        }
    }

    private boolean areArgumentsValid(List<String> arguments) {
        if (arguments.size() < 2) {
            return false;
        }
        String firstArg = arguments.get(0);
        return ADD.equalsIgnoreCase(firstArg) || REMOVE.equalsIgnoreCase(firstArg);
    }

    @Override
    public Permission getRequiredPermission() {
        return Permission.HIDE_COMMAND;
    }
}
