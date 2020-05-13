package eu.cloudnetservice.v2.master.api.event.server;

import de.dytanic.cloudnet.event.async.AsyncEvent;
import de.dytanic.cloudnet.event.async.AsyncPosterAdapter;
import eu.cloudnetservice.v2.master.network.components.MinecraftServer;

/**
 * Created by Tareko on 17.08.2017.
 */
public class ServerAddEvent extends AsyncEvent<ServerAddEvent> {

    private MinecraftServer minecraftServer;

    public ServerAddEvent(MinecraftServer minecraftServer) {
        super(new AsyncPosterAdapter<>());
        this.minecraftServer = minecraftServer;
    }

    public MinecraftServer getMinecraftServer() {
        return minecraftServer;
    }
}