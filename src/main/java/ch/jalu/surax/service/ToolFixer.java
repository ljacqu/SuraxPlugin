package ch.jalu.surax.service;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Merges tools of the same type if the common durability doesn't exceed the max durability.
 */
public class ToolFixer {

    private static final Comparator<ItemStack> DURABILITY_DESC_COMPARATOR = new DurabilityDescComparator();
    private final Inventory inventory;

    private final List<Material> changedMaterials = new ArrayList<>();

    public ToolFixer(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * The material (tool) to merge in the inventory where possible.
     *
     * @param material the material
     */
    public void fixUpTool(Material material) {
        final Map<Integer, ItemStack> items = getAllWithoutEnchantmentsSortByDurability(material);
        final short maxDurability = material.getMaxDurability();

        List<ItemStack> newItems = new ArrayList<>();
        List<Integer> slotsToDelete = new ArrayList<>();

        short curInverseDurabilitySum = 0;
        List<Integer> currentSlots = new ArrayList<>();
        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            final int inverseDurability = maxDurability - entry.getValue().getDurability();
            if (curInverseDurabilitySum + inverseDurability <= maxDurability) {
                curInverseDurabilitySum += inverseDurability;
                currentSlots.add(entry.getKey());
            } else {
                if (currentSlots.size() > 1) {
                    newItems.add(computeNewMaterial(material, curInverseDurabilitySum));
                    slotsToDelete.addAll(currentSlots);

                    curInverseDurabilitySum = entry.getValue().getDurability();
                    currentSlots.clear();
                    currentSlots.add(entry.getKey());
                } else {
                    // Since items are sorted by durability, if we're here it means we won't be able
                    // to merge any items anymore
                    break;
                }
            }
        }
        if (currentSlots.size() > 1) {
            newItems.add(computeNewMaterial(material, curInverseDurabilitySum));
            slotsToDelete.addAll(currentSlots);
        }

        if (!newItems.isEmpty()) {
            slotsToDelete.forEach(i -> inventory.setItem(i, null));
            newItems.forEach(inventory::addItem);
            changedMaterials.add(material);
        }
    }


    private ItemStack computeNewMaterial(Material material, int inverseDurabilitySum) {
        short newDurability = (short) Math.max(material.getMaxDurability() - inverseDurabilitySum * 1.05, 0);
        ItemStack item = new ItemStack(material);
        item.setDurability(newDurability);
        return item;
    }

    public List<Material> getChangedMaterials() {
        return changedMaterials;
    }

    private Map<Integer, ItemStack> getAllWithoutEnchantmentsSortByDurability(Material material) {
        Map<Integer, ? extends ItemStack> all = inventory.all(material);
        return all.entrySet().stream()
            .filter(e -> e.getValue().getEnchantments().isEmpty())
            .filter(e -> e.getValue().getAmount() == 1) // just to be sure
            .filter(e -> e.getValue().getDurability() <= material.getMaxDurability()) // just to be sure
            .sorted(Map.Entry.comparingByValue(DURABILITY_DESC_COMPARATOR))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new));
    }

    private static final class DurabilityDescComparator implements Comparator<ItemStack> {
        @Override
        public int compare(ItemStack o1, ItemStack o2) {
            return ((Short) o2.getDurability()).compareTo(o1.getDurability());
        }
    }

}
