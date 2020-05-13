package eu.cloudnetservice.v2.bridge.event.proxied;

import eu.cloudnetservice.v2.lib.server.info.ServerInfo;

/**
 * This event is called whenever a server has been added to the CloudNet network.
 * When receiving this event, the server might not be done with starting up.
 */
public class ProxiedServerAddEvent extends ProxiedCloudEvent {

    private ServerInfo serverInfo;

    public ProxiedServerAddEvent(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    /**
     * The server information object about the server that has been added to the network.
     *
     * @return the server information of the added server.
     */
    public ServerInfo getServerInfo() {
        return serverInfo;
    }
}
