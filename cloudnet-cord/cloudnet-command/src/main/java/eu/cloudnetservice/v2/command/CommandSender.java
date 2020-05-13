package eu.cloudnetservice.v2.command;

import eu.cloudnetservice.v2.lib.interfaces.Nameable;

/**
 * Interface for denoting classes that can dispatch commands.
 */
public interface CommandSender extends Nameable {

    /**
     * Send messages to this command sender.
     *
     * @param message the messages to send
     */
    void sendMessage(String... message);

    /**
     * Query this command sender for its permissions.
     *
     * @param permission the permission in question
     *
     * @return {@code true} when this command sender has the queried permission,
     * {@code false} otherwise.
     */
    boolean hasPermission(String permission);

}
