/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.user;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.hash.DyHash;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Tareko on 17.09.2017.
 */
public class BasicUser extends User {

    public BasicUser(String name, String hashedPassword, Collection<String> permissions) {
        super(name, UUID.randomUUID(), NetworkUtils.randomString(16), DyHash.hashString(hashedPassword), permissions, new HashMap<>());
    }
}
