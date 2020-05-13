package eu.cloudnetservice.v2.master.api.event.network;

import de.dytanic.cloudnet.event.Event;
import eu.cloudnetservice.v2.master.network.NetworkManager;

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