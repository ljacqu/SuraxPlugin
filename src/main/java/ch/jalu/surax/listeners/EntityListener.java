package ch.jalu.surax.listeners;

import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import static ch.jalu.surax.util.Utils.randomInt;

/**
 * Listener for entity events.
 */
public class EntityListener implements Listener {

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Sheep) {
            processSheepSpawn((Sheep) entity, event);
        }
    }

    private void processSheepSpawn(Sheep sheep, CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.BREEDING) {
            DyeColor[] dyes = DyeColor.values();
            sheep.setColor(dyes[randomInt(dyes.length)]);
        }
    }
}
