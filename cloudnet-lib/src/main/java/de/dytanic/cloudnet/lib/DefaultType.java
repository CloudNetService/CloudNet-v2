/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public enum DefaultType {
    BUKKIT,
    BUNGEE_CORD;

    public static final Type TYPE = TypeToken.get(DefaultType.class).getType();
}
