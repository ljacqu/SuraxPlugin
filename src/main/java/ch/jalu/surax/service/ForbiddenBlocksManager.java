package ch.jalu.surax.service;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Keeps track of players placing items at forbidden heights.
 */
public class ForbiddenBlocksManager {

    private static final EnumSet FORBIDDEN_ITEMS = EnumSet.of(Material.SUGAR_CANE_BLOCK, Material.CACTUS);
    private static final int MIN_Y_COORD = 50;
    private static final String JAIL_COMMAND = "jail %p jail3 1min";

    private final Set<UUID> alertedPlayers = new HashSet<>();
    private final Random random = new Random();


    public void handleEvent(BlockPlaceEvent event) {
        if (FORBIDDEN_ITEMS.contains(event.getBlock().getType()) && MIN_Y_COORD < event.getBlock().getY()) {
            event.setCancelled(true);
            handleBlockViolation(event.getPlayer());
        }
    }

    private void handleBlockViolation(Player player) {
        int randId = 0;
        if (!alertedPlayers.contains(player.getUniqueId())) {
            alertedPlayers.add(player.getUniqueId());
        } else {
            randId = random.nextInt(4);
        }

        if (randId == 1) {
            player.sendMessage(ChatColor.RED
                + "You've been jailed: don't place drug plants above Y coordinate " + MIN_Y_COORD + "!");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), JAIL_COMMAND.replace("%p", player.getName()));
        } else {
            alertedPlayers.add(player.getUniqueId());
            player.sendMessage(ChatColor.RED
                + "Careful! You may not place drug plants above Y coordinate " + MIN_Y_COORD);
        }
    }
}
