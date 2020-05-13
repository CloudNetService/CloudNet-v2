package eu.cloudnetservice.v2.bridge.event.proxied;

import eu.cloudnetservice.v2.lib.server.info.ServerInfo;

/**
 * This event is called when a server has been removed from the CloudNet network.
 * When receiving this event, the server has already been stopped.
 */
public class ProxiedServerRemoveEvent extends ProxiedCloudEvent {

    private ServerInfo serverInfo;

    public ProxiedServerRemoveEvent(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    /**
     * The server information object of the removed server.
     *
     * @return the information of the removed server.
     */
    public ServerInfo getServerInfo() {
        return serverInfo;
    }
}
