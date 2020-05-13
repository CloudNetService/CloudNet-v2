package eu.cloudnetservice.v2.bridge.event.proxied;

import eu.cloudnetservice.v2.lib.server.info.ServerInfo;

/**
 * This event is called whenever server information is updated on the network.
 */
public class ProxiedServerInfoUpdateEvent extends ProxiedCloudEvent {

    private final ServerInfo serverInfo;

    public ProxiedServerInfoUpdateEvent(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    /**
     * @return the updated server information.
     */
    public ServerInfo getServerInfo() {
        return serverInfo;
    }
}
