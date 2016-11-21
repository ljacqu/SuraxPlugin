package ch.jalu.surax.commands;

import ch.jalu.surax.Permission;
import org.bukkit.World;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * Implementation of {@code /glow}.
 */
public class GlowCommand extends PlayerCommand {

    public void execute(World world) {
        Collection<Bat> bats = world.getEntitiesByClass(Bat.class);
        bats.forEach(bat -> bat.setGlowing(true));
    }

    @Override
    public String getName() {
        return "glow";
    }

    @Nullable
    @Override
    public Permission getRequiredPermission() {
        return Permission.GLOW_COMMAND;
    }

    @Override
    protected void execute(Player player, List<String> arguments) {
        if (arguments.size() != 2) {
            player.sendMessage("Usage: /glow bat on, or /glow zombie off");
            return;
        }

        EntityType type = toEntityType(arguments.get(0));
        if (type == null) {
            player.sendMessage("Unknown entity type! Do /glow for examples");
            return;
        }

        final World world = player.getWorld();
        if ("on".equalsIgnoreCase(arguments.get(1))) {
            world.getEntitiesByClass(type.getEntityClass())
                .forEach(e -> e.setGlowing(true));
            player.sendMessage("All entities of type " + formatType(type) + " are now glowing");
        } else if ("off".equalsIgnoreCase(arguments.get(1))) {
            world.getEntitiesByClass(type.getEntityClass())
                .forEach(e -> e.setGlowing(false));
            player.sendMessage("All entities of type " + formatType(type) + " are no longer glowing");
        } else {
            player.sendMessage("Unknown state! Use e.g. /glow zombie off or /glow zombie on");
        }
    }

    private static String formatType(EntityType type) {
        return type.name().toLowerCase().replace("_", " ");
    }

    @Nullable
    private static EntityType toEntityType(String str) {
        for (EntityType type : EntityType.values()) {
            if (str.equalsIgnoreCase(type.name())) {
                return type;
            }
        }
        return null;
    }
}
