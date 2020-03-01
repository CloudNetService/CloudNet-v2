package de.dytanic.cloudnet.api.builders;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.network.packet.api.PacketOutStartServer;
import de.dytanic.cloudnet.lib.process.ServerProcessBuilder;
import de.dytanic.cloudnet.lib.process.ServerProcessData;
import de.dytanic.cloudnet.lib.server.ServerProcessMeta;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Builder for a server process in the API for proxies and servers.
 * Uses {@link ServerProcessData} for storing the data.
 */
public class ApiServerProcessBuilder extends ServerProcessBuilder {

    /**
     * Creates a new server process builder for a server of the specified server group.
     * This value is mandatory as servers cannot be started without belonging to a group.
     *
     * @param serverGroupName the name of the server group that the server will be started from.
     *
     * @return the newly created server process builder.
     */
    public static ServerProcessBuilder create(String serverGroupName) {
        return new ApiServerProcessBuilder().serverGroupName(serverGroupName);
    }

    /**
     * Requests the master to initiate this server startup.
     * This will trigger a sequence of messages to be passed between the calling service,
     * the master and the wrapper that will start the server.
     * <p>
     * Once the server is started, the returned future will be completed and contains the
     * process metadata that was valid at the time of starting up.
     * <p>
     * Note that the delay between requesting the start (ie. this method) and the actual
     * completion of the returned future is indefinite and the future may not actually
     * be completed at all-
     *
     * @return a future that will be completed once the server is connected to the cloud network.
     */
    public CompletableFuture<ServerProcessMeta> startServer() {
        final UUID uuid = UUID.randomUUID();
        this.getServerProcessData().getServerConfig().getProperties().append("cloudnet:requestId", uuid);
        CloudAPI.getInstance().getNetworkConnection().sendAsynchronous(
            new PacketOutStartServer(this.getServerProcessData())
        );
        return CloudAPI.getInstance().getCloudService().waitForServer(uuid);
    }

}
