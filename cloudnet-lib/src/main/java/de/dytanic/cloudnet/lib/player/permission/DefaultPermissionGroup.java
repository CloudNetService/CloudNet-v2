/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.player.permission;

import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;

import java.util.*;

/**
 * Created by Tareko on 20.08.2017.
 */
public class DefaultPermissionGroup extends PermissionGroup {

    public DefaultPermissionGroup(String name)
    {
        super(name, "§e", "§f", "§7", 98, 0, false, new HashMap<>(), new HashMap<>(), new HashMap<>(), new ArrayList<>());
    }
}