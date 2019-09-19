/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.command;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.player.permission.Permissible;

/**
 * Interface for denoting classes that can dispatch commands.
 */
public interface CommandSender extends Nameable, Permissible {

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
