package ch.jalu.surax.commands;

import ch.jalu.surax.Permission;
import ch.jalu.surax.config.InvisibilityConfig;
import ch.jalu.surax.service.InvisibilityManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

import static ch.jalu.surax.util.MessageUtils.outputList;

/**
 * Implementation for {@code /unhide}.
 */
public class UnhideCommand implements Command {

    @Inject
    private InvisibilityConfig config;
    @Inject
    private InvisibilityManager invisibilityManager;

    UnhideCommand() {
    }

    @Override
    public String getName() {
        return "unhide";
    }

    @Override
    public void execute(CommandSender sender, List<String> arguments) {
        if (arguments.size() < 1) {
            sender.sendMessage("Need at least two arguments! E.g. /unhide bobby clarence");
        } else if (arguments.size() == 1) {
            outputList("Player '" + arguments.get(0) + "' is hidden from", config.getBlockedPlayers(arguments.get(0)))
                .orIfEmpty("Player '" + arguments.get(0) + "' is hidden from no one")
                .to(sender);
        } else {
            final String hider = arguments.get(0);
            final List<String> hidees = arguments.subList(1, arguments.size());
            final Player player = Bukkit.getPlayer(hider);

            config.removeBlockedPlayer(sender.getName(), hidees);
            invisibilityManager.processUnhide(player, hidees);
            sender.sendMessage(hider + " is no longer hidden from " + String.join(", ", hidees));
        }
    }

    @Override
    public Permission getRequiredPermission() {
        return Permission.HIDE_COMMAND;
    }
}
