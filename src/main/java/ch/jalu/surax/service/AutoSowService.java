package ch.jalu.surax.service;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Service for auto-sowing.
 */
public class AutoSowService {

    private Set<UUID> players = new HashSet<>();

    public void processPlayerMove(Player player) {
        if (!players.contains(player.getUniqueId()) || player.isFlying()) {
            return;
        }

        // player.location is at the player's feet, but because soil is lower it will return the soil block
        // he is standing on
        final Block groundBlock = player.getLocation().getBlock();
        if (Material.SOIL != groundBlock.getType()) {
            return;
        }
        final Block blockAtFeet = groundBlock.getRelative(BlockFace.UP);
        if (Material.AIR != blockAtFeet.getType()) {
            return;
        }

        if (!player.getInventory().contains(Material.SEEDS)) {
            players.remove(player.getUniqueId());
            player.sendMessage("You don't have seeds anymore. Turning auto-sow off.");
        } else {
            player.getInventory().removeItem(new ItemStack(Material.SEEDS, 1));
            blockAtFeet.setType(Material.CROPS);
        }
    }

    public boolean toggle(Player player) {
        if (players.add(player.getUniqueId())) {
            return true;
        }
        players.remove(player.getUniqueId());
        return false;
    }
}
