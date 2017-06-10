package ch.jalu.surax.service;

import ch.jalu.surax.domain.Home;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.IUser;
import com.earth2me.essentials.User;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

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
    private Logger logger;

    private IEssHandler handler = new IEssHandler() {};

    EssentialsHook() {
    }

    @Override
    protected String getPluginClass() {
        return "com.earth2me.essentials.Essentials";
    }

    @Override
    public String getPluginName() {
        return "Essentials";
    }

    @Override
    protected void acceptPlugin(Plugin plugin) {
        handler = new EssentialsHandler((Essentials) plugin, logger);
    }

    @Override
    public void unhook() {
        handler = new IEssHandler() {};
    }

    @Nullable
    public List<Home> getHomesInWorld(String player, World world) {
        return handler.getHomesInWorld(player, world);
    }

    public void processHide(String hider, String hidee) {
        handler.processHide(hider, hidee);
    }

    public void processUnhide(String hider, String hidee) {
        handler.processUnhide(hider, hidee);
    }

    private interface IEssHandler {

        default void processHide(String hider, String hidee) {
        }

        default void processUnhide(String hider, String hidee) {
        }

        default List<Home> getHomesInWorld(String player, World world) {
            return null;
        }
    }

    // Inner class so we can check for the presence of Essentials before we do anything with Essentials classes
    private static final class EssentialsHandler implements IEssHandler {

        private final Essentials essentials;
        private final Logger logger;

        EssentialsHandler(Essentials essentials, Logger logger) {
            this.essentials = essentials;
            this.logger = logger;
        }

        @Nullable
        @Override
        public List<Home> getHomesInWorld(String player, World world) {
            User user = essentials.getUser(player);
            if (user == null) {
                logger.info("Could not get user '" + player + "' from Essentials");
                return null;
            }
            return getHomesInWorld(user, world);
        }

        @Override
        public void processHide(String hider, String hidee) {
            User hiderPlayer = essentials.getUser(hider);
            User hideePlayer = essentials.getUser(hidee);
            if (hiderPlayer != null && hideePlayer != null) {
                hideePlayer.setIgnoredPlayer(hiderPlayer, true);
            }
        }

        @Override
        public void processUnhide(String hider, String hidee) {
            User hiderPlayer = essentials.getUser(hider);
            User hideePlayer = essentials.getUser(hidee);
            if (hiderPlayer != null && hideePlayer != null) {
                hideePlayer.setIgnoredPlayer(hiderPlayer, false);
            }
        }

        private static List<Home> getHomesInWorld(IUser user, World world) {
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
        private static Location getHomeSilently(IUser user, String home) {
            try {
                return user.getHome(home);
            } catch (Exception e) {
                // silent
            }
            return null;
        }
    }
}
