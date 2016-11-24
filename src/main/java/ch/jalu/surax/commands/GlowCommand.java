package ch.jalu.surax.commands;

import ch.jalu.surax.Permission;
import org.bukkit.World;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
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

    @Override
    public Permission getRequiredPermission() {
        return Permission.GLOW_COMMAND;
    }

    @Override
    protected void execute(Player player, List<String> arguments) {
        if (arguments.size() < 1) {
            player.sendMessage("Usage: /glow bat, or /glow zombie off");
            return;
        }

        GlowParemeter parameters = stringToGlowParameter(arguments.get(0));
        if (parameters == null) {
            player.sendMessage("Unknown entity type! Do /glow for examples");
            return;
        }

        final World world = player.getWorld();
        if (arguments.size() < 2 || "on".equalsIgnoreCase(arguments.get(1))) {
            world.getEntitiesByClass(parameters.entityClass).forEach(e -> e.setGlowing(true));
            player.sendMessage("All " + parameters.type + " entities are now glowing");
        } else if ("off".equalsIgnoreCase(arguments.get(1))) {
            world.getEntitiesByClass(parameters.entityClass).forEach(e -> e.setGlowing(false));
            player.sendMessage("All " + parameters.type + " entities are no longer glowing");
        } else {
            player.sendMessage("Unknown state! Use e.g. /glow zombie off or /glow zombie on");
        }
    }

    @Nullable
    private GlowParemeter stringToGlowParameter(String type) {
        if ("all".equalsIgnoreCase(type)) {
            return new GlowParemeter("", Entity.class);
        } else if ("monster".equalsIgnoreCase(type)) {
            return new GlowParemeter("monster", Monster.class);
        } else {
            EntityType entityType = toEntityType(type);
            if (entityType != null) {
                return new GlowParemeter(formatType(entityType), entityType.getEntityClass());
            }
        }

        return null;
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

    private static final class GlowParemeter {
        private String type;
        private Class<? extends Entity> entityClass;

        GlowParemeter(String type, Class<? extends Entity> entityClass) {
            this.type = type;
            this.entityClass = entityClass;
        }
    }
}
