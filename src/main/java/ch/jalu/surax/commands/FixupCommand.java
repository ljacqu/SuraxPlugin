package ch.jalu.surax.commands;

import ch.jalu.surax.Permission;
import ch.jalu.surax.service.ToolFixer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

import static org.bukkit.Material.DIAMOND_AXE;
import static org.bukkit.Material.DIAMOND_HOE;
import static org.bukkit.Material.DIAMOND_PICKAXE;
import static org.bukkit.Material.DIAMOND_SPADE;
import static org.bukkit.Material.DIAMOND_SWORD;
import static org.bukkit.Material.FISHING_ROD;
import static org.bukkit.Material.FLINT_AND_STEEL;
import static org.bukkit.Material.GOLD_AXE;
import static org.bukkit.Material.GOLD_HOE;
import static org.bukkit.Material.GOLD_PICKAXE;
import static org.bukkit.Material.GOLD_SPADE;
import static org.bukkit.Material.GOLD_SWORD;
import static org.bukkit.Material.IRON_AXE;
import static org.bukkit.Material.IRON_HOE;
import static org.bukkit.Material.IRON_PICKAXE;
import static org.bukkit.Material.IRON_SPADE;
import static org.bukkit.Material.IRON_SWORD;
import static org.bukkit.Material.SHEARS;
import static org.bukkit.Material.STONE_AXE;
import static org.bukkit.Material.STONE_HOE;
import static org.bukkit.Material.STONE_PICKAXE;
import static org.bukkit.Material.STONE_SPADE;
import static org.bukkit.Material.STONE_SWORD;
import static org.bukkit.Material.WOOD_AXE;
import static org.bukkit.Material.WOOD_HOE;
import static org.bukkit.Material.WOOD_PICKAXE;
import static org.bukkit.Material.WOOD_SPADE;
import static org.bukkit.Material.WOOD_SWORD;

/**
 * Implementation for {@code /fixup}: groups broken tools of the same kind as can be done manually.
 * Only crafts tools together if no durability gets lost.
 */
public class FixupCommand extends PlayerCommand {

    private static final Material[] FIXABLE_TOOLS = {
        FLINT_AND_STEEL, SHEARS, FISHING_ROD,
        WOOD_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, GOLD_PICKAXE, DIAMOND_PICKAXE,
        WOOD_SPADE,   STONE_SPADE,   IRON_SPADE,   GOLD_SPADE,   DIAMOND_SPADE,
        WOOD_HOE,     STONE_HOE,     IRON_HOE,     GOLD_HOE,     DIAMOND_HOE,
        WOOD_SWORD,   STONE_SWORD,   IRON_SWORD,   GOLD_SWORD,   DIAMOND_SWORD,
        WOOD_AXE,     STONE_AXE,     IRON_AXE,     GOLD_AXE,     DIAMOND_AXE
    };

    @Override
    public String getName() {
        return "fixup";
    }

    @Override
    public Permission getRequiredPermission() {
        return Permission.FIXUP_COMMAND;
    }

    @Override
    protected void execute(Player player, List<String> arguments) {
        ToolFixer toolFixer = new ToolFixer(player.getInventory());

        for (Material material : FIXABLE_TOOLS) {
            toolFixer.fixUpTool(material);
        }

        List<Material> changedMaterials = toolFixer.getChangedMaterials();
        if (changedMaterials.isEmpty()) {
            player.sendMessage("Could not fix up any tools");
        } else {
            player.sendMessage("Could fix tools: "
                + changedMaterials.stream()
                    .map(m -> m.name().toLowerCase().replace("_", " "))
                    .collect(Collectors.joining(", ")));
        }
    }


}
