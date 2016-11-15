package ch.jalu.surax.commands;

import ch.jalu.surax.Permission;
import ch.jalu.surax.config.InvisibilityConfig;
import ch.jalu.surax.service.InvisibilityManager;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

/**
 * Implementation of the {@code /unhideme} command.
 */
public class UnhideMeCommand extends PlayerCommand {

    @Inject
    private InvisibilityConfig config;
    @Inject
    private InvisibilityManager invisibilityManager;

    UnhideMeCommand() {
    }

    @Override
    public String getName() {
        return "unhideme";
    }

    @Override
    public void execute(Player sender, List<String> arguments) {
        if (arguments.isEmpty()) {
            sender.sendMessage("You are hidden from: " + config.getBlockedPlayers(sender.getName()));
        } else {
            config.removeBlockedPlayer(sender.getName(), arguments);
            invisibilityManager.processUnhide(sender, arguments);
            sender.sendMessage("You are now unhidden from players " + arguments);
        }
    }

    @Override
    public Permission getRequiredPermission() {
        return Permission.HIDE_ME_COMMAND;
    }
}
