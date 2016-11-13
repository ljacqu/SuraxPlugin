package ch.jalu.surax.domain;

import org.bukkit.Location;

/**
 * Represents an Essentials home.
 */
public class Home {

    private final String name;
    private final Location location;

    public Home(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }
}
