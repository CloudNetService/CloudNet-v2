/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.player;

import de.dytanic.cloudnet.lib.player.permission.Permissible;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.utility.document.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class OfflinePlayer implements Nameable, Permissible {

    protected UUID uniqueId;

    protected String name;

    protected Document metaData;

    protected Long lastLogin;

    protected Long firstLogin;

    protected PlayerConnection lastPlayerConnection;

    protected PermissionEntity permissionEntity;
}