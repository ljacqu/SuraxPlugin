package ch.jalu.surax.service;

import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Hooks into WorldGuard.
 */
public class WorldGuardHook extends AbstractPluginHook {

    private static final String WORLD_NAME = "world";
    private static final String REGION_NAME = "spawn_market_entrance";

    @Inject
    private Logger logger;

    private IWorldGuardHandler handler = new IWorldGuardHandler() {};

    public boolean isInProtectedRegion(Location location) {
        return handler.isInProtectedRegion(location);
    }

    @Override
    protected String getPluginClass() {
        return "com.sk89q.worldguard.bukkit.WorldGuardPlugin";
    }

    @Override
    public String getPluginName() {
        return "WorldGuard";
    }

    @Override
    protected void acceptPlugin(Plugin plugin) {
        handler = new WorldGuardHandler((WorldGuardPlugin) plugin, logger);
    }

    @Override
    public void unhook() {
        handler = new IWorldGuardHandler() {};
    }

    private interface IWorldGuardHandler {
        default boolean isInProtectedRegion(Location location) {
            return false;
        }
    }

    private static final class WorldGuardHandler implements IWorldGuardHandler {

        private final WorldGuardPlugin worldGuard;
        private final Logger logger;
        private ProtectedRegion noTpRegion;

        WorldGuardHandler(WorldGuardPlugin worldGuard, Logger logger) {
            this.worldGuard = worldGuard;
            this.logger = logger;
        }

        @Override
        public boolean isInProtectedRegion(Location location) {
            ProtectedRegion region = getRegion();
            return region != null && region.contains(BukkitUtil.toVector(location));
        }

        @Nullable
        private ProtectedRegion getRegion() {
            if (noTpRegion != null) {
                return noTpRegion;
            } else if (worldGuard == null) {
                return null;
            }

            RegionManager regions = worldGuard.getRegionContainer().get(Bukkit.getWorld(WORLD_NAME));
            if (regions != null) {
                noTpRegion = regions.getRegion(REGION_NAME);
                if (noTpRegion == null) {
                    logger.warning("Could not get region '" + REGION_NAME + "' from WorldGuard");
                }
                return noTpRegion;
            }
            logger.warning("Could not get regions in '" + WORLD_NAME + "' from WorldGuard");
            return null;
        }
    }
}
