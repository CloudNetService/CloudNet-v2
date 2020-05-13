package eu.cloudnetservice.v2.master.api.event.server;

import eu.cloudnetservice.v2.event.Event;
import eu.cloudnetservice.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.v2.master.network.components.INetworkComponent;

/**
 * Calls if one server updates his serverInfo
 */
public class ServerInfoUpdateEvent extends Event {

    private final INetworkComponent minecraftServer;
    private final ServerInfo serverInfo;

    public ServerInfoUpdateEvent(INetworkComponent minecraftServer, ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        this.minecraftServer = minecraftServer;
    }

    public INetworkComponent getMinecraftServer() {
        return minecraftServer;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }
}
