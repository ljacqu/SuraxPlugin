package ch.jalu.surax.commands;

import ch.jalu.surax.Permission;
import ch.jalu.surax.service.FreezeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

/**
 * Implementation for the {@code /freeze} command.
 */
public class FreezeCommand implements Command {

    @Inject
    private FreezeManager manager;

    @Override
    public String getName() {
        return "freeze";
    }

    @Override
    public void execute(CommandSender sender, List<String> arguments) {
        if (arguments.isEmpty()) {
            sender.sendMessage("The following players are frozen: " + String.join(", ", manager.getAllPlayers()));
        } else {
            Player player = Bukkit.getPlayerExact(arguments.get(0));
            manager.addPlayer(arguments.get(0));
            sender.sendMessage("Player '" + arguments.get(0) + "' is now frozen");
            if (player == null) {
                sender.sendMessage(ChatColor.RED + " Warning: Player '" + arguments.get(0) + "' is not online");
            } else {
                player.sendMessage("You have been frozen!");
            }
        }
    }

    @Override
    public Permission getRequiredPermission() {
        return Permission.FREEZE_PLAYER;
    }
}
