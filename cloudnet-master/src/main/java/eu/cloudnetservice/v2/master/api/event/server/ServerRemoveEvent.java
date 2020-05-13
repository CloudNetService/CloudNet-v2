package eu.cloudnetservice.v2.master.api.event.server;

import eu.cloudnetservice.v2.event.async.AsyncEvent;
import eu.cloudnetservice.v2.event.async.AsyncPosterAdapter;
import eu.cloudnetservice.v2.master.network.components.MinecraftServer;

/**
 * Calls if one server is removed and unwhitelisted from a wrapper
 */
public class ServerRemoveEvent extends AsyncEvent<ServerRemoveEvent> {

    private final MinecraftServer minecraftServer;

    public ServerRemoveEvent(MinecraftServer minecraftServer) {
        super(new AsyncPosterAdapter<>());
        this.minecraftServer = minecraftServer;
    }

    public MinecraftServer getMinecraftServer() {
        return minecraftServer;
    }
}
