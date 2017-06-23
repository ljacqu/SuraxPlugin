package ch.jalu.surax.listeners;

import ch.jalu.surax.Permission;
import ch.jalu.surax.config.PvpStorage;
import ch.jalu.surax.config.UntargetRules;
import ch.jalu.surax.service.AutoSowService;
import ch.jalu.surax.service.DrugItemsService;
import ch.jalu.surax.service.ForbiddenBlocksManager;
import ch.jalu.surax.service.WorldGuardHook;
import com.google.common.collect.ImmutableSet;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import javax.inject.Inject;
import java.util.Set;
import java.util.logging.Logger;

import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.CLOSEST_ENTITY;
import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.CLOSEST_PLAYER;
import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.CUSTOM;
import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.PIG_ZOMBIE_TARGET;
import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.RANDOM_TARGET;
import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.REINFORCEMENT_TARGET;
import static org.bukkit.event.entity.EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY;

/**
 * Listener of player events.
 */
public class PlayerListener implements Listener {

    private static final Set<TargetReason> SUPPRESSED_REASONS = ImmutableSet.of(
        CLOSEST_ENTITY, CLOSEST_PLAYER, PIG_ZOMBIE_TARGET, RANDOM_TARGET, TARGET_ATTACKED_NEARBY_ENTITY,
        REINFORCEMENT_TARGET, CUSTOM);

    @Inject
    private AutoSowService autoSowService;
    @Inject
    private PvpStorage pvpStorage;
    @Inject
    private UntargetRules untargetRules;
    @Inject
    private WorldGuardHook worldGuardHook;
    @Inject
    private ForbiddenBlocksManager forbiddenBlocksManager;
    @Inject
    private DrugItemsService drugItemsService;
    @Inject
    private Logger logger;

    @EventHandler
    public void onMobTarget(EntityTargetEvent event) {
        Player target = event.getTarget() instanceof Player ? (Player) event.getTarget() : null;
        EntityType source = event.getEntity() == null ? null : event.getEntity().getType();
        if (target != null && source != null && untargetRules.shouldSuppress(target.getName(), source)) {
            TargetReason reason = event.getReason();
            if (SUPPRESSED_REASONS.contains(reason)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent event) {
        final Player damagedPlayer = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;
        if (event.getDamager() instanceof Player && damagedPlayer != null) {
            if (!pvpStorage.doesPlayerHavePvp(event.getDamager().getName())
                || !pvpStorage.doesPlayerHavePvp(damagedPlayer.getName())) {
                event.getDamager().sendMessage("PVP is disabled for you or the target");
                event.setCancelled(true);
            }
        } else if (damagedPlayer != null && drugItemsService.isEnforcerZombie(event.getDamager())) {
            drugItemsService.processIfHasDrugItems(damagedPlayer);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        autoSowService.processPlayerMove(event.getPlayer());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        forbiddenBlocksManager.handleEvent(event);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        if (Permission.WORLDGUARD_TP_BLOCK_OVERRIDE.allows(player)) {
            // Player is allowed to TP - don't check
            return;
        }
        Location destination = event.getTo();
        if (worldGuardHook.isInProtectedRegion(destination) && !worldGuardHook.isInProtectedRegion(event.getFrom())) {
            event.setTo(new Location(event.getTo().getWorld(), 94.5, 63, 261.5)); // TP to spawn instead
            player.sendMessage(ChatColor.RED + "You may not teleport into this region!");
            logger.info("Player '" + player.getName() + "' tried to TP into protected WG region");
        }
    }
}
