/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.user;

import de.dytanic.cloudnet.lib.hash.DyHash;
import de.dytanic.cloudnet.lib.interfaces.Nameable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tareko on 11.09.2017.
 */
@EqualsAndHashCode
@ToString
public class User implements Nameable {

    protected String name;

    protected UUID uniqueId;

    protected String apiToken;

    protected String hashedPassword;

    protected Collection<String> permissions;

    protected Map<String, Object> metaData;

    public User(String name, UUID uniqueId, String apiToken, String hashedPassword, Collection<String> permissions, Map<String, Object> metaData) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.apiToken = apiToken;
        this.hashedPassword = hashedPassword;
        this.permissions = permissions;
        this.metaData = metaData;
    }

    public String getApiToken() {
        return apiToken;
    }

    @Override
    public String getName() {
        return name;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public Collection<String> getPermissions() {
        return permissions;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public SimpledUser toSimple()
    {
        return new SimpledUser(name, apiToken);
    }

    public boolean hasPermission(String permission)
    {
        return permissions.contains("*") || permissions.contains(permission);
    }

    public void setPassword(String password) {
        this.hashedPassword = DyHash.hashString(password);
    }
}