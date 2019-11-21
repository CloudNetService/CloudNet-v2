/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.user.permission;

import de.dytanic.cloudnet.lib.user.User;

import java.util.UUID;

/**
 * Created by Tareko on 27.09.2017.
 */
public class UserablePermissionEntity {

    private final UUID uniqueId;
    private User user;

    public UserablePermissionEntity(UUID uniqueId, User user) {
        this.uniqueId = uniqueId;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public boolean hasPermission(String permission) {
        return user.getPermissions().contains(permission.toLowerCase()) || user.getPermissions().contains("*");
    }

    public UUID getUniqueId() {
        return uniqueId;
    }
}
