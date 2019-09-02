/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.user.permission;

import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnet.lib.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Tareko on 27.09.2017.
 */
public class UserablePermissionEntity extends PermissionEntity {

    private User user;

    public UserablePermissionEntity(UUID uniqueId, User user) {
        super(uniqueId, new HashMap<>(), "User | ", "", new ArrayList<>());
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public boolean hasPermission(String permission) {
        return user.getPermissions().contains(permission.toLowerCase()) || user.getPermissions().contains("*");
    }

    @Deprecated
    @Override
    public boolean hasPermission(PermissionPool permissionPool, String permission, String group) {
        throw new UnsupportedOperationException("User hasPermissio(String permission);");
    }
}
