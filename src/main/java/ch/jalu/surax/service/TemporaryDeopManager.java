package ch.jalu.surax.service;

import ch.jalu.surax.SuraxPlugin;
import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Keeps track of temporarily deopped players.
 */
public class TemporaryDeopManager {

    private static final long DEOP_DURATION = 60 * 20; // 60s

    @Inject
    private SuraxPlugin plugin;
    @Inject
    private Logger logger;

    private Map<String, BukkitRunnable> temporarilyDeopped = new ConcurrentHashMap<>();

    TemporaryDeopManager() {
    }

    public void tempDeopPlayer(Player player) {
        final String name = player.getName().toLowerCase();
        if (temporarilyDeopped.containsKey(name)) {
            logger.warning("Not temporarily deopping '" + player.getName() + "': entry already exists!");
        } else {
            Preconditions.checkArgument(player.isOp(), "Player '" + player.getName() + "' should be OP!");
            player.setOp(false);
            temporarilyDeopped.put(name, scheduleReopTask(name));
        }
    }

    public void reopPlayer(Player player) {
        final String name = player.getName().toLowerCase();
        if (temporarilyDeopped.containsKey(name)) {
            player.setOp(true);
            temporarilyDeopped.get(name).cancel();
            temporarilyDeopped.remove(name.toLowerCase());
        } else {
            throw new IllegalStateException("Player '" + player.getName() + "' is not temp deopped");
        }
    }

    public boolean isTemporarilyDeopped(String name) {
        return temporarilyDeopped.containsKey(name.toLowerCase());
    }

    private BukkitRunnable scheduleReopTask(String name) {
        ReopTask reopTask = new ReopTask(name);
        reopTask.runTaskLater(plugin, DEOP_DURATION);
        return reopTask;
    }


    private final class ReopTask extends BukkitRunnable {

        private final String name;

        ReopTask(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            Player player = Bukkit.getPlayerExact(name);
            if (player != null) {
                player.setOp(true);
                player.sendMessage("You are opped again!");
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
                if (offlinePlayer == null) {
                    logger.warning("Could not reop player '" + name + "': is offline, lacking OfflinePlayer object");
                } else {
                    offlinePlayer.setOp(true);
                    logger.warning("Player '" + name + "' was offline: attempted to re-op offline player");
                }
            }
            temporarilyDeopped.remove(name);
        }
    }

}
