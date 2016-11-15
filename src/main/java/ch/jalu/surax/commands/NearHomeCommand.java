package ch.jalu.surax.commands;

import ch.jalu.surax.domain.Home;
import ch.jalu.surax.service.EssentialsHook;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the {@code /nearhome} command.
 */
public class NearHomeCommand implements Command {

    private static final int MAX_ENTRIES = 5;

    @Inject
    private EssentialsHook essentialsHook;

    NearHomeCommand() {
    }

    @Override
    public String getName() {
        return "nearhome";
    }

    @Override
    public void execute(CommandSender sender, List<String> arguments) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to run this command");
            return;
        }

        Multimap<Double, String> homesByDistance = getHomesByDistance((Player) sender, arguments.contains("-flat"));
        if (homesByDistance != null) {
            int shownEntries = 0;
            for (Map.Entry<Double, String> entry : homesByDistance.entries()) {
                sender.sendMessage("/home " + entry.getValue() + " (" + entry.getKey().intValue() + " blocks)");
                if (++shownEntries > MAX_ENTRIES) {
                    break;
                }
            }
        }
    }

    @Nullable
    private Multimap<Double, String> getHomesByDistance(Player player, boolean isFlat) {
        final Location location = player.getLocation();
        List<Home> homesInWorld = essentialsHook.getHomesInWorld(player.getName(), location.getWorld());
        if (homesInWorld == null) {
            player.sendMessage("Could not get your homes - is Essentials not hooked?");
            return null;
        } else if (homesInWorld.isEmpty()) {
            player.sendMessage("You do not have any homes");
            return null;
        }

        Multimap<Double, String> homesByDistance = TreeMultimap.create();
        for (Home home : homesInWorld) {
            if (isFlat) {
                home.getLocation().setY(location.getY());
            }
            homesByDistance.put(location.distance(home.getLocation()), home.getName());
        }
        return homesByDistance;
    }
}
