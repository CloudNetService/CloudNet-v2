/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.api.builders;

import eu.cloudnetservice.cloudnet.v2.api.CloudAPI;
import eu.cloudnetservice.cloudnet.v2.api.network.packet.api.PacketOutStartServer;
import eu.cloudnetservice.cloudnet.v2.lib.process.ServerProcessBuilder;
import eu.cloudnetservice.cloudnet.v2.lib.process.ServerProcessData;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerProcessMeta;

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
