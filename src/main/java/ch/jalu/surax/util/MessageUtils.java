package ch.jalu.surax.util;

import org.bukkit.command.CommandSender;

import java.util.Collection;

/**
 * Utility for messages.
 */
public final class MessageUtils {

    private MessageUtils() {
    }

    /**
     * Formats an enum for output.
     *
     * @param entry the enum entry
     * @param <E> the enum type
     * @return the enum entry as a string
     */
    public static <E extends Enum<E>> String formatEnum(E entry) {
        return entry.name().toLowerCase().replace("_", " ");
    }

    /**
     * Sends a message based on a prefix and a collection.
     *
     * @param message the message to show before the list
     * @param elements the collection to turn into a comma-separated list
     * @return the message
     */
    public static ListSender outputList(String message, Collection<String> elements) {
        return new ListSender(message, elements);
    }

    public static final class ListSender {

        private final String message;
        private final String elements;
        private String emptyMessage;

        private ListSender(String message, Collection<String> elements) {
            this.message = message;
            this.elements = String.join(", ", elements);
        }

        /**
         * Message to send if the collection is empty.
         *
         * @param emptyMessage the empty message
         * @return the list sender instance
         */
        public ListSender orIfEmpty(String emptyMessage) {
            this.emptyMessage = emptyMessage;
            return this;
        }

        /**
         * Sends the appropriate message to the sender.
         *
         * @param sender the sender to send the message to
         */
        public void to(CommandSender sender) {
            if ("".equals(elements) && emptyMessage != null) {
                sender.sendMessage(emptyMessage);
            } else {
                sender.sendMessage(message + ": " + elements);
            }
        }
    }

}
