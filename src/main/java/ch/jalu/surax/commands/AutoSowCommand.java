package ch.jalu.surax.commands;

import ch.jalu.surax.Permission;
import ch.jalu.surax.service.AutoSowService;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

/**
 * Implementation for {@code /autosow} command.
 */
public class AutoSowCommand extends PlayerCommand {

    @Inject
    private AutoSowService autoSowService;

    @Override
    public String getName() {
        return "autosow";
    }

    @Override
    public Permission getRequiredPermission() {
        return Permission.AUTO_SOW;
    }

    @Override
    protected void execute(Player player, List<String> arguments) {
        boolean isEnabled = autoSowService.toggle(player);
        if (isEnabled) {
            player.sendMessage("You are now auto-sowing");
        } else {
            player.sendMessage("Auto-sow has been turned off");
        }
    }
}
