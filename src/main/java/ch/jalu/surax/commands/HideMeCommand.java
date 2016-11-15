package ch.jalu.surax.commands;

import ch.jalu.surax.Permission;
import ch.jalu.surax.config.InvisibilityConfig;
import ch.jalu.surax.service.InvisibilityManager;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

/**
 * Implementation of the {@code /hideme} command.
 */
public class HideMeCommand extends PlayerCommand {

    @Inject
    private InvisibilityConfig config;
    @Inject
    private InvisibilityManager invisibilityManager;

    HideMeCommand() {
    }

    @Override
    public String getName() {
        return "hideme";
    }

    @Override
    public void execute(Player sender, List<String> arguments) {
        if (arguments.isEmpty()) {
            sender.sendMessage("You are hidden from: " + config.getBlockedPlayers(sender.getName()));
        } else {
            config.addBlockedPlayer(sender.getName(), arguments);
            invisibilityManager.processHide(sender, arguments);
            sender.sendMessage("You are now hidden from players " + arguments);
        }
    }

    @Override
    public Permission getRequiredPermission() {
        return Permission.HIDE_ME_COMMAND;
    }
}
