package ch.jalu.surax.commands;

import ch.jalu.surax.Permission;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * Implementation of {@code /bakeall}.
 */
public class BakeAllCommand extends PlayerCommand {

    @Override
    public String getName() {
        return "bakeall";
    }

    @Override
    public Permission getRequiredPermission() {
        return Permission.BAKE_ALL_COMMAND;
    }

    @Override
    protected void execute(Player player, List<String> arguments) {
        final Inventory inventory = player.getInventory();
        Map<Integer, ? extends ItemStack> allBread = inventory.all(Material.WHEAT);
        int totalWheat = allBread.values().stream()
            .mapToInt(ItemStack::getAmount)
            .sum();

        int totalBread = totalWheat / 3;
        if (totalBread == 0) {
            player.sendMessage("You don't have enough wheat in your inventory");
            return;
        }

        inventory.remove(Material.WHEAT);
        inventory.addItem(new ItemStack(Material.BREAD, totalBread));
        // e.g. 61 wheat -> 20 bread + 1 wheat
        int remainingWheat = totalWheat - (totalBread * 3);
        if (remainingWheat > 0) {
            inventory.addItem(new ItemStack(Material.WHEAT, remainingWheat));
        }
        String pluralS = totalBread == 1 ? "" : "s";
        player.sendMessage("You now have " + totalBread + " bread" + pluralS);
    }
}
