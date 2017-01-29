package ch.jalu.surax.listeners;

import ch.jalu.surax.config.UntargetRules;
import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import javax.inject.Inject;
import java.util.Set;

import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.CLOSEST_ENTITY;
import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.CLOSEST_PLAYER;
import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.CUSTOM;
import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.PIG_ZOMBIE_TARGET;
import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.RANDOM_TARGET;
import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.REINFORCEMENT_TARGET;
import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY;

/**
 * Listener for when an entity targets a player.
 */
public class EntityTargetListener implements Listener {

    private static final Set<TargetReason> SUPPRESSED_REASONS = ImmutableSet.of(
        CLOSEST_ENTITY, CLOSEST_PLAYER, PIG_ZOMBIE_TARGET, RANDOM_TARGET, TARGET_ATTACKED_NEARBY_ENTITY,
        REINFORCEMENT_TARGET, CUSTOM);

    @Inject
    private UntargetRules untargetRules;

    @EventHandler
    public void onMobTarget(EntityTargetEvent event) {
        Player target = event.getTarget() instanceof Player ? (Player) event.getTarget() : null;
        EntityType source = event.getEntity() == null ? null : event.getEntity().getType();
        if (target != null && source != null & untargetRules.shouldSuppress(target.getName(), source)) {
            TargetReason reason = event.getReason();
            if (SUPPRESSED_REASONS.contains(reason)) {
                event.setCancelled(true);
            }
        }
    }
}
