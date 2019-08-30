/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.wrapper;

import de.dytanic.cloudnetcore.network.components.WrapperMeta;

import java.util.UUID;

/**
 * Created by Tareko on 23.09.2017.
 */
public class WrapperSession {

    private UUID uniqueId;

    private WrapperMeta wrapperMeta;

    private long connected;

    public WrapperSession(UUID uniqueId, WrapperMeta wrapperMeta, long connected) {
        this.uniqueId = uniqueId;
        this.wrapperMeta = wrapperMeta;
        this.connected = connected;
    }

    public long getConnected() {
        return connected;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public WrapperMeta getWrapperMeta() {
        return wrapperMeta;
    }
}