package ch.jalu.surax.domain;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static org.bukkit.Material.COAL;
import static org.bukkit.Material.COBBLESTONE;
import static org.bukkit.Material.GOLD_INGOT;
import static org.bukkit.Material.IRON_INGOT;
import static org.bukkit.Material.STICK;
import static org.bukkit.Material.WOOD;


public enum ToolRecipe {

    WOOD_PICKAXE(pickaxe(WOOD)),

    STONE_PICKAXE(pickaxe(COBBLESTONE)),

    IRON_PICKAXE(pickaxe(IRON_INGOT)),

    GOLD_PICKAXE(pickaxe(GOLD_INGOT)),

    WOOD_SWORD(sword(WOOD)),

    STONE_SWORD(sword(COBBLESTONE)),

    IRON_SWORD(sword(IRON_INGOT)),

    GOLD_SWORD(sword(GOLD_INGOT)),

    WOOD_SPADE(spade(WOOD)),

    STONE_SPADE(spade(COBBLESTONE)),

    IRON_SPADE(spade(IRON_INGOT)),

    GOLD_SPADE(spade(GOLD_INGOT)),

    WOOD_AXE(axe(WOOD)),

    STONE_AXE(axe(COBBLESTONE)),

    IRON_AXE(axe(IRON_INGOT)),

    GOLD_AXE(axe(GOLD_INGOT)),

    WOOD_HOE(hoe(WOOD)),

    STONE_HOE(hoe(COBBLESTONE)),

    IRON_HOE(hoe(IRON_INGOT)),

    GOLD_HOE(hoe(GOLD_INGOT)),

    TORCH(of(item(STICK, 1), item(COAL, 1)), item(Material.TORCH, 4));

    private final List<ItemStack> recipe;
    private ItemStack result;

    ToolRecipe(List<ItemStack> recipe) {
        this.recipe = recipe;
    }

    ToolRecipe(List<ItemStack> recipe, ItemStack result) {
        this.recipe = recipe;
        this.result = result;
    }

    public static void init() {
        for (ToolRecipe recipe : values()) {
            if (recipe.result == null) {
                recipe.result = item(Material.valueOf(recipe.name()), 1);
            }
        }
    }

    @Nullable
    public static ToolRecipe getRecipe(Material material) {
        for (ToolRecipe recipe : values()) {
            if (recipe.result.getType() == material) {
                return recipe;
            }
        }
        return null;
    }

    public List<ItemStack> getRecipe() {
        return recipe;
    }

    public ItemStack getResult() {
        return result;
    }

    // =========================
    // Generic recipes
    // =========================
    private static List<ItemStack> pickaxe(Material material) {
        return of(item(material, 3), item(STICK, 2));
    }

    private static List<ItemStack> sword(Material material) {
        return of(item(material, 2), item(STICK, 1));
    }

    private static List<ItemStack> spade(Material material) {
        return of(item(material, 1), item(STICK, 2));
    }

    private static List<ItemStack> axe(Material material) {
        return of(item(material, 3), item(STICK, 2));
    }

    private static List<ItemStack> hoe(Material material) {
        return of(item(material, 2), item(STICK, 2));
    }

    private static ItemStack item(Material m, int amount) {
        return new ItemStack(m, amount);
    }
}