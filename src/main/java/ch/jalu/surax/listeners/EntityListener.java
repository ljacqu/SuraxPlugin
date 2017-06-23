package ch.jalu.surax.listeners;

import ch.jalu.surax.service.WorldGuardHook;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import javax.inject.Inject;

import static ch.jalu.surax.util.Utils.passWithProbability;
import static ch.jalu.surax.util.Utils.randomInt;

/**
 * Listener for entity events.
 */
public class EntityListener implements Listener {

    @Inject
    private WorldGuardHook worldGuardHook;

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Zombie) {
            processZombieSpawn((Zombie) entity, event);
        } else if (entity instanceof Sheep) {
            processSheepSpawn((Sheep) entity);
        }
    }

    private void processZombieSpawn(Zombie zombie, CreatureSpawnEvent event) {
        if (zombie.isBaby() || zombie instanceof ZombieVillager) {
            return;
        }

        if (passWithProbability(25) || worldGuardHook.isInProtectedRegion(event.getLocation())) {
            final ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
            LeatherArmorMeta armorMeta = (LeatherArmorMeta) helmet.getItemMeta();
            armorMeta.setColor(Color.fromRGB(0, 60, 120));
            helmet.setItemMeta(armorMeta);
            zombie.getEquipment().setHelmet(helmet);
            zombie.getEquipment().setItemInOffHand(new ItemStack(Material.STICK));
            zombie.setHealth(zombie.getHealth() * 0.8);
        }
    }

    private void processSheepSpawn(Sheep sheep) {
        DyeColor[] dyes = DyeColor.values();
        sheep.setColor(dyes[randomInt(dyes.length)]);
    }
}
