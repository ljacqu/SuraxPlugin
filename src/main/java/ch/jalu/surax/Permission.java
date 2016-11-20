package ch.jalu.surax;

import org.bukkit.command.CommandSender;

/**
 * Permission for the plugin.
 */
public enum Permission {

    NEAR_HOME("surax.nearhome"),

    HIDE_COMMAND("surax.hide"),

    HIDE_ME_COMMAND("surax.hideme"),

    PVP_COMMAND("surax.pvp"),

    PVP_OTHERS("surax.pvp.others"),

    FREEZE_PLAYER("surax.freeze"),

    TEMPORARY_DEOP("surax.tdeop"),

    BAKE_ALL_COMMAND("surax.bakeall"),

    FIXUP_COMMAND("surax.fixup");

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