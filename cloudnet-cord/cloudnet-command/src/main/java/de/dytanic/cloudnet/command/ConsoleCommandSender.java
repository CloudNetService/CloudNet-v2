/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.command;

import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Class that defines a command sender in a terminal.
 * An instance of this class has all permissions, a random UUID and the name {@code CONSOLE}
 */
public class ConsoleCommandSender implements CommandSender {

    private PermissionEntity permissionEntity = new PermissionEntity(UUID.randomUUID(),
                                                                     new HashMap<>(),
                                                                     "§cCloud §7| ",
                                                                     "§f",
                                                                     new LinkedList<>());

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public void sendMessage(String... message) {
        CollectionWrapper.iterator(message, System.out::println);
    }

    @Override
    public boolean hasPermission(String permission) {
        return true; // CONSOLE has all permissions
    }

    @Override
    public PermissionEntity getPermissionEntity() {
        return permissionEntity;
    }
}
