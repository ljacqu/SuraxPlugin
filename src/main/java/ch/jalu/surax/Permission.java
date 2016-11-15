package ch.jalu.surax;

import org.bukkit.command.CommandSender;

/**
 * Permission for the plugin.
 */
public enum Permission {

    NEAR_HOME("surax.nearhome"),

    HIDE_COMMAND("surax.hide"),

    HIDE_ME_COMMAND("surax.hideme");

    private final String node;

    Permission(String node) {
        this.node = node;
    }

    public String getNode() {
        return node;
    }

    public boolean allows(CommandSender sender) {
        return sender.hasPermission(node);
    }
}