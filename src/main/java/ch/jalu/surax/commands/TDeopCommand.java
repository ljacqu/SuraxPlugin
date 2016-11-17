package ch.jalu.surax.commands;

import ch.jalu.surax.Permission;
import ch.jalu.surax.service.TemporaryDeopManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implementation of {@code /tdeop}.
 */
public class TDeopCommand extends PlayerCommand {

    @Inject
    private TemporaryDeopManager manager;
    @Inject
    private Logger logger;

    @Override
    public String getName() {
        return "tdeop";
    }

    @Override
    protected void execute(Player player, List<String> arguments) {
        if (player.isOp()) {
            manager.tempDeopPlayer(player);
            player.sendMessage("Temp deop! Use /reop or wait 1 min to recover OP status");
        } else {
            player.sendMessage(ChatColor.RED + "You are not opped!");
            logger.warning("Player '" + player.getName() + "' did /tdeop but is not OP");
        }
    }

    @Override
    public Permission getRequiredPermission() {
        return Permission.TEMPORARY_DEOP;
    }
}
