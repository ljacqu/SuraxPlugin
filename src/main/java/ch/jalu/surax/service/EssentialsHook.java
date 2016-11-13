package ch.jalu.surax.service;

import ch.jalu.surax.domain.Home;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.IUser;
import com.earth2me.essentials.User;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Hooks into Essentials.
 */
public class EssentialsHook {

    @Inject
    private PluginManager pluginManager;
    @Inject
    private Logger logger;

    private boolean isHooked;
    private Essentials essentials;

    @PostConstruct
    public void hook() {
        if (!isEssentialsClassLoaded()) {
            logger.info("Essentials class not loaded - hook process aborted");
            return;
        }

        Plugin plugin = pluginManager.getPlugin("Essentials");
        if (plugin instanceof Essentials) {
            essentials = (Essentials) plugin;
            isHooked = true;
            logger.info("Hooked into Essentials " + essentials.getDescription().getVersion());
        } else if (plugin != null) {
            logger.warning("Essentials plugin is of unexpected type '" + plugin.getClass() + "'");
        } else {
            logger.info("Could not hook into Essentials - unavailable");
        }
    }

    public void unhook() {
        essentials = null;
        isHooked = false;
    }

    @Nullable
    public List<Home> getHomesInWorld(String player, World world) {
        if (!isHooked) {
            return null;
        }

        User user = essentials.getUser(player);
        if (user == null) {
            logger.info("Could not get user '" + player + "' from Essentials");
            return null;
        }
        return getHomesInWorld(user, world);
    }

    private List<Home> getHomesInWorld(IUser user, World world) {
        List<Home> homes = new ArrayList<>();
        for (String name : user.getHomes()) {
            Location location = getHomeSilently(user, name);
            if (location != null && location.getWorld().equals(world)) {
                homes.add(new Home(name, location));
            }
        }
        return homes;
    }

    @Nullable
    private Location getHomeSilently(IUser user, String home) {
        try {
            return user.getHome(home);
        } catch (Exception e) {
            // silent
        }
        return null;
    }

    private static boolean isEssentialsClassLoaded() {
        try {
            Class.forName("com.earth2me.essentials.Essentials");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
