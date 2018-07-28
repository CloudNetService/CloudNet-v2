/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.command;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.player.permission.Permissible;

/**
 * Created by Tareko on 23.05.2017.
 */
public interface CommandSender
        extends Nameable, Permissible {

    void sendMessage(String... message);

    boolean hasPermission(String permission);

}
