/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command.sender;

import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.user.permission.UserablePermissionEntity;

import java.util.UUID;

/**
 * Created by Tareko on 27.09.2017.
 */
public class UserCommandSender implements CommandSender {

    private User user;
    private PermissionEntity permissionEntity;

    public UserCommandSender(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void sendMessage(String... message) {
        for (String m : message) {
            System.out.println(m);
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return ((UserablePermissionEntity) getPermissionEntity()).hasPermission(permission);
    }

    @Override
    public PermissionEntity getPermissionEntity() {
        if (permissionEntity == null) {
            permissionEntity = new UserablePermissionEntity(UUID.randomUUID(), user);
        }
        return permissionEntity;
    }
}
