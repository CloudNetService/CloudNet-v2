/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.player;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.player.permission.Permissible;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.dytanic.cloudnet.lib.utility.document.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.UUID;

public class OfflinePlayer implements Nameable, Permissible {

    public static final Type TYPE = new TypeToken<OfflinePlayer>() {
    }.getType();

    protected UUID uniqueId;

    protected String name;

    protected Document metaData;

    protected Long lastLogin;

    protected Long firstLogin;

    protected PlayerConnection lastPlayerConnection;

    protected PermissionEntity permissionEntity;

    public OfflinePlayer(UUID uniqueId, String name, Document metaData, Long lastLogin, Long firstLogin, PlayerConnection lastPlayerConnection, PermissionEntity permissionEntity) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.metaData = metaData;
        this.lastLogin = lastLogin;
        this.firstLogin = firstLogin;
        this.lastPlayerConnection = lastPlayerConnection;
        this.permissionEntity = permissionEntity;
    }

    @Override
    public String getName() {
        return name;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public Document getMetaData() {
        return metaData;
    }

    public Long getFirstLogin() {
        return firstLogin;
    }

    public Long getLastLogin() {
        return lastLogin;
    }

    @Override
    public PermissionEntity getPermissionEntity() {
        return permissionEntity;
    }

    public PlayerConnection getLastPlayerConnection() {
        return lastPlayerConnection;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFirstLogin(Long firstLogin) {
        this.firstLogin = firstLogin;
    }

    public void setLastLogin(Long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setLastPlayerConnection(PlayerConnection lastPlayerConnection) {
        this.lastPlayerConnection = lastPlayerConnection;
    }

    public void setMetaData(Document metaData) {
        this.metaData = metaData;
    }

    public void setPermissionEntity(PermissionEntity permissionEntity) {
        this.permissionEntity = permissionEntity;
    }
}