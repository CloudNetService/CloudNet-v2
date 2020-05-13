package eu.cloudnetservice.v2.master.api.event.server;

import de.dytanic.cloudnet.event.Event;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import eu.cloudnetservice.v2.master.network.components.INetworkComponent;

/**
 * Calls if one server updates his serverInfo
 */
public class ServerInfoUpdateEvent extends Event {

    private INetworkComponent minecraftServer;
    private ServerInfo serverInfo;

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
