package ch.jalu.surax.commands;

import ch.jalu.surax.Permission;
import ch.jalu.surax.domain.ToolRecipe;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

import static ch.jalu.surax.util.MessageUtils.formatEnum;

/**
 * Implementation for {@code /craft}.
 */
public class CraftCommand extends PlayerCommand {

    @Inject
    private Logger logger;

    @Override
    public String getName() {
        return "craft";
    }

    @Override
    public Permission getRequiredPermission() {
        return Permission.CRAFT_COMMAND;
    }

    @Override
    protected void execute(Player player, List<String> arguments) {
        final PlayerInventory inventory = player.getInventory();
        Material selectedItem = inventory.getItemInMainHand().getType();
        ToolRecipe recipe = ToolRecipe.getRecipe(selectedItem);
        if (recipe == null) {
            player.sendMessage("No recipe known for " + formatEnum(selectedItem));
            return;
        }

        if (checkHasAllMaterials(recipe.getRecipe(), inventory)) {
            recipe.getRecipe().forEach(inventory::removeItem);
            inventory.addItem(recipe.getResult());
            player.sendMessage("Crafted a new " + formatEnum(selectedItem) + " for you");
            logger.info("Crafted " + selectedItem + " for " + player.getName());
        } else {
            player.sendMessage("Cannot craft tool; you don't have the required materials.");
        }
    }

    private boolean checkHasAllMaterials(List<ItemStack> items, Inventory inventory) {
        int requiredSticks = 0;
        for (ItemStack is : items) {
            if (!inventory.contains(is.getType(), is.getAmount())) {
                if (Material.STICK == is.getType()) {
                    requiredSticks = is.getAmount();
                } else {
                    return false;
                }
            }
        }

        return requiredSticks == 0 || craftSticks(inventory, requiredSticks);
    }

    private boolean craftSticks(Inventory inventory, int requiredSticks) {
        int availableSticks = totalInInventory(inventory, Material.STICK);
        int missingSticks = roundUpToEven(requiredSticks - availableSticks);

        for (StickProducer stickProducer : StickProducer.values()) {
            if (stickProducer.canProduce(missingSticks, inventory)) {
                logger.info("Crafting at least " + missingSticks + " sticks for " + inventory.getHolder());
                stickProducer.craft(missingSticks, inventory);
                return true;
            }
        }
        return false;
    }

    private static int totalInInventory(Inventory inventory, Material material) {
        return inventory.all(material).values().stream()
            .mapToInt(ItemStack::getAmount).sum();
    }

    /**
     * Rounds up to the next even number, e.g. returns 6 for 6, and 4 for 3.
     *
     * @param i the number
     * @return next higher even number
     */
    private static int roundUpToEven(int i) {
        return (i & 1) == 0 ? i : ++i;
    }

    private static int ceil(double d) {
        return (int) Math.ceil(d);
    }

    /**
     * Blocks with which we can craft sticks.
     */
    private enum StickProducer {

        WOOD(Material.WOOD,
            2,
            // e.g. need 6 sticks -> 3 wood, but it needs to be rounded up to 4
            i -> roundUpToEven(ceil(i / 2d))),

        LOG(Material.LOG,
            8,
            i -> ceil(i / 8d)),

        LOG_2(Material.LOG_2,
            8,
            i -> ceil(i / 8d));

        private Material source;
        private int conversionFactor;
        private Function<Integer, Integer> requiredItems;

        /**
         * Constructor.
         *
         * @param source the material from which sticks can be crafted
         * @param conversionFactor how many sticks one source item crafts
         * @param requiredItems function returning how many source items are needed to craft the given number of sticks
         */
        StickProducer(Material source, int conversionFactor, Function<Integer, Integer> requiredItems) {
            this.source = source;
            this.conversionFactor = conversionFactor;
            this.requiredItems = requiredItems;
        }

        private int getRequiredItems(int desiredSticks) {
            return requiredItems.apply(desiredSticks);
        }

        boolean canProduce(int desiredSticks, Inventory inventory) {
            return totalInInventory(inventory, source) >= getRequiredItems(desiredSticks);
        }

        void craft(int desiredSticks, Inventory inventory) {
            int sourceItems = getRequiredItems(desiredSticks);
            int newSticks = sourceItems * conversionFactor;
            inventory.removeItem(new ItemStack(source, sourceItems));
            inventory.addItem(new ItemStack(Material.STICK, newSticks));
        }
    }

}
