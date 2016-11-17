package ch.jalu.surax.commands;

import ch.jalu.surax.Permission;
import ch.jalu.surax.service.TemporaryDeopManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implementation of {@code /reop}.
 */
public class ReopCommand extends PlayerCommand {

    @Inject
    private TemporaryDeopManager manager;
    @Inject
    private Logger logger;

    @Override
    public String getName() {
        return "reop";
    }

    @Override
    protected void execute(Player player, List<String> arguments) {
        if (manager.isTemporarilyDeopped(player.getName())) {
            manager.reopPlayer(player);
            player.sendMessage("You are opped again");
        } else {
            player.sendMessage(ChatColor.RED + "You are not opped!");
            logger.warning("Player '" + player.getName() + "' did /reop but is not temp deopped");
        }
    }

    @Override
    public Permission getRequiredPermission() {
        // When a player is temporarily deopped, he doesn't necessarily have this permission anymore
        // Regular players will just receive a "You are not opped" message...
        return null;
        // return Permission.TEMPORARY_DEOP;
    }
}
