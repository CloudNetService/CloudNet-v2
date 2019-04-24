/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.user;

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
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class User implements Nameable {

    protected String name;

    protected UUID uniqueId;

    protected String apiToken;

    protected String hashedPassword;

    protected Collection<String> permissions;

    protected Map<String, Object> metaData;

    public SimpledUser toSimple() {
        return new SimpledUser(name, apiToken);
    }

    public boolean hasPermission(String permission) {
        return permissions.contains("*") || permissions.contains(permission);
    }

}