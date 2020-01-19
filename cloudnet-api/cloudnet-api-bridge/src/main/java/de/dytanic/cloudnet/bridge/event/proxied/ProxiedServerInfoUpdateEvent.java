package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.server.info.ServerInfo;

/**
 * This event is called whenever server information is updated on the network.
 */
public class ProxiedServerInfoUpdateEvent extends ProxiedCloudEvent {

    private ServerInfo serverInfo;

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
