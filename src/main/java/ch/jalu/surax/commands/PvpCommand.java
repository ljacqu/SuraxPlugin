package ch.jalu.surax.commands;

import ch.jalu.surax.Permission;
import ch.jalu.surax.config.PvpStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

/**
 * Command to toggle pvp on or off.
 */
public class PvpCommand implements Command {

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
    public void execute(CommandSender sender, List<String> arguments) {
        if (arguments.size() < 1) {
            handleNoArgCall(sender);
        } else if (arguments.size() == 1) {
            handleOneArgCall(sender, arguments.get(0));
        } else {
            handleTwoArgCall(sender, arguments.get(0), arguments.get(1));
        }
    }

    private void handleNoArgCall(CommandSender sender) {
        if (sender instanceof Player) {
            if (pvpStorage.doesPlayerHavePvp(sender.getName())) {
                sender.sendMessage("You have PVP enabled!");
            } else {
                sender.sendMessage("PVP is disabled for you");
            }
        } else {
            sender.sendMessage("For player: /pvp on or /pvp off to set PVP status");
            sender.sendMessage("For admin/console: use /pvp on <player> or /pvp off <player>");
        }
    }

    private void handleOneArgCall(CommandSender sender, String arg) {
        if (isOnOrOff(arg)) {
            if (sender instanceof Player) {
                if (ON.equalsIgnoreCase(arg)) {
                    pvpStorage.removeDisabledPvpPlayer(sender.getName());
                    sender.sendMessage("You have PVP enabled!");
                } else {
                    pvpStorage.addDisabledPvpPlayer(sender.getName());
                    sender.sendMessage("PVP is disabled for you");
                }
            } else {
                sender.sendMessage("Player only, or use /pvp on <player>");
            }
        }
        else {
            if (Permission.PVP_OTHERS.allows(sender)) {
                boolean isEnabled = pvpStorage.doesPlayerHavePvp(arg);
                sender.sendMessage(isEnabled
                    ? "Player '" + arg + "' has PVP on"
                    : "Player '" + arg + "' has PVP disabled");
            } else {
                sender.sendMessage("No permission!");
            }
        }
    }

    private void handleTwoArgCall(CommandSender sender, String arg1, String arg2) {
        if (ON.equalsIgnoreCase(arg1)) {
            pvpStorage.removeDisabledPvpPlayer(arg2);
            sender.sendMessage("Set player '" + arg2 + "' to have PVP");
            sendToPlayerIfOnline(arg2, "PVP has been turned on!");
        } else if (OFF.equalsIgnoreCase(arg1)) {
            pvpStorage.addDisabledPvpPlayer(arg2);
            sender.sendMessage("Disabled PVP for player '" + arg2 + "'");
            sendToPlayerIfOnline(arg2, "PVP has been turned off for you");
        } else {
            sender.sendMessage("Usage: /pvp on <player>, /pvp off <player>");
        }
    }

    private static boolean isOnOrOff(String str) {
        return ON.equalsIgnoreCase(str) || OFF.equalsIgnoreCase(str);
    }

    private static void sendToPlayerIfOnline(String name, String message) {
        Player player = Bukkit.getPlayerExact(name);
        if (player != null) {
            player.sendMessage(message);
        }
    }

    @Override
    public Permission getRequiredPermission() {
        return Permission.PVP_COMMAND;
    }
}
