package ch.jalu.surax.service;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nullable;

import static ch.jalu.surax.util.Utils.randomInt;
import static java.lang.String.valueOf;

/**
 * Handles features around drug items.
 */
public class DrugItemsService {

    public boolean hasDrugItems(PlayerInventory inv) {
        return inv.contains(Material.SUGAR)
            || inv.contains(Material.MUSHROOM_SOUP); // TODO cactus green
    }

    private void removeIllegalItems(Inventory inventory) {
        inventory.remove(Material.SUGAR);
        inventory.remove(Material.MUSHROOM_SOUP);
        // TODO cactus green
    }

    public void processIfHasDrugItems(Player player) {
        if (hasDrugItems(player.getInventory())) {
            switch (randomInt(3)) {
                case 0: warnAndTeleportPlayer(player); break;
                case 1: removeIllegalItems(player.getInventory()); // fall through
                case 2: jailPlayer(player); break;
                default: /* noop - should not occur */
            }
        }
    }

    public boolean isEnforcerZombie(Entity entity) {
        if (entity instanceof Zombie) {
            EntityEquipment equipment = ((Zombie) entity).getEquipment();
            if (isMaterial(equipment.getItemInOffHand(), Material.STICK)
                && isMaterial(equipment.getHelmet(), Material.LEATHER_HELMET)) {
                return true;
            }
        }
        return false;
    }

    private void jailPlayer(Player player) {
        int seconds = 30 + randomInt(60);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "jail %p jail1 %ssec".replace("%p", player.getName()).replace("%s", valueOf(seconds)));
        player.sendMessage(ChatColor.RED + "Illegal items have been found in your inventory! You are now in jail");
    }

    private void warnAndTeleportPlayer(Player player) {
        player.sendMessage(ChatColor.RED + "Illegal items have been found in your inventory! Back to spawn you go!");
        Bukkit.dispatchCommand(player, "spawn");
    }

    private static boolean isMaterial(@Nullable ItemStack itemStack, Material expectedMaterial) {
        return itemStack != null && itemStack.getType() == expectedMaterial;
    }
}
