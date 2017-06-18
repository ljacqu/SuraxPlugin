package ch.jalu.surax.listeners;

import ch.jalu.surax.service.WorldGuardHook;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.inject.Inject;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Listener of ChestShop events.
 */
public class ChestShopListener implements Listener {

    private static final Set<String> FORBIDDEN_ITEM_NAMES = ImmutableSet.of(
        "Sugar Cane", "Sugar", "Mushroom Stew", "Red Mushroom", "Brown Mushroom", "Green Ink Sack"
    );

    @Inject
    private WorldGuardHook worldGuardHook;
    @Inject
    private Logger logger;

    private Random random = new Random();

    @EventHandler
    public void onShopCreate(PreShopCreationEvent event) {
        final String itemLine = event.getSignLines()[3];
        if (FORBIDDEN_ITEM_NAMES.contains(itemLine) && !worldGuardHook.isInProtectedRegion(event.getSign().getLocation())) {
            event.setOutcome(PreShopCreationEvent.CreationOutcome.INVALID_ITEM);
            final Player player = event.getPlayer();
            player.sendMessage(ChatColor.RED + "You may not create shops with drug items out in the open! See /info underground");
            if (random.nextInt(4) == 1) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "jail %p jail2 1min".replace("%p", player.getName()));
            }
        }
    }
}
