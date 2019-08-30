/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.network;

import de.dytanic.cloudnet.event.Event;
import de.dytanic.cloudnetcore.network.NetworkManager;

/**
 * Calls if the NetworkManager.updateAll(); method is invoked
 */
public class UpdateAllEvent extends Event {

    private NetworkManager networkManager;

    private boolean isOnlineCloudNetworkUpdate;

    public UpdateAllEvent(NetworkManager networkManager, boolean isOnlineCloudNetworkUpdate) {
        this.networkManager = networkManager;
        this.isOnlineCloudNetworkUpdate = isOnlineCloudNetworkUpdate;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public boolean isOnlineCloudNetworkUpdate() {
        return isOnlineCloudNetworkUpdate;
    }
}