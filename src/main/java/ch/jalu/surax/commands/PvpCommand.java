package ch.jalu.surax.commands;

import ch.jalu.surax.Permission;
import ch.jalu.surax.config.PvpStorage;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

/**
 * Command to toggle pvp on or off.
 */
public class PvpCommand extends PlayerCommand {

    private static final String ON = "on";
    private static final String OFF = "off";

    @Inject
    private PvpStorage pvpStorage;

    PvpCommand() {
    }

    @Override
    public String getName() {
        return "pvp";
    }

    @Override
    protected void execute(Player player, List<String> arguments) {
        if (arguments.size() < 1) {
            if (pvpStorage.doesPlayerHavePvp(player.getName())) {
                player.sendMessage("You have PVP enabled!");
            } else {
                player.sendMessage("PVP is disabled for you");
            }
        } else if (ON.equalsIgnoreCase(arguments.get(0))) {
            pvpStorage.removeDisabledPvpPlayer(player.getName());
            player.sendMessage("You have PVP enabled!");
        } else if (OFF.equalsIgnoreCase(arguments.get(0))) {
            pvpStorage.addDisabledPvpPlayer(player.getName());
            player.sendMessage("PVP is disabled for you");
        } else {
            player.sendMessage("Usage: /pvp on or /pvp off");
        }
    }

    @Override
    public Permission getRequiredPermission() {
        return Permission.PVP_COMMAND;
    }
}
