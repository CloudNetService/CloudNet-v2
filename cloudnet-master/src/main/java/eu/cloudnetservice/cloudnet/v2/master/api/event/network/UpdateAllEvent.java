package eu.cloudnetservice.cloudnet.v2.master.api.event.network;

import eu.cloudnetservice.cloudnet.v2.event.Event;
import eu.cloudnetservice.cloudnet.v2.master.network.NetworkManager;

/**
 * Calls if the NetworkManager.updateAll(); method is invoked
 */
public class UpdateAllEvent extends Event {

    private final NetworkManager networkManager;

    private final boolean isOnlineCloudNetworkUpdate;

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