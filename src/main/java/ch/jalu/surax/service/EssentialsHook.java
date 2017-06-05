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
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Hooks into Essentials.
 */
public class EssentialsHook extends AbstractPluginHook {

    @Inject
    private PluginManager pluginManager;
    @Inject
    private Logger logger;

    private boolean isHooked;
    private Essentials essentials;

    @Override
    protected String getPluginClass() {
        return "com.earth2me.essentials.Essentials";
    }

    @Override
    protected String getPluginName() {
        return "Essentials";
    }

    @Override
    protected void acceptPlugin(Plugin plugin) {
        essentials = (Essentials) plugin;
        isHooked = true;
    }

    @Override
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

    public void processHide(String hider, String hidee) {
        if (isHooked) {
            User hiderPlayer = essentials.getUser(hider);
            User hideePlayer = essentials.getUser(hidee);
            if (hiderPlayer != null && hideePlayer != null) {
                hideePlayer.setIgnoredPlayer(hiderPlayer, true);
            }
        }
    }

    public void processUnhide(String hider, String hidee) {
        if (isHooked) {
            User hiderPlayer = essentials.getUser(hider);
            User hideePlayer = essentials.getUser(hidee);
            if (hiderPlayer != null && hideePlayer != null) {
                hideePlayer.setIgnoredPlayer(hiderPlayer, false);
            }
        }
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
}
