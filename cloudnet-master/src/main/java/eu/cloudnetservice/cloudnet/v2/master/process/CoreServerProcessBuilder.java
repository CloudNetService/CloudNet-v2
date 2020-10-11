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

package eu.cloudnetservice.cloudnet.v2.master.process;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.process.ServerProcessBuilder;
import eu.cloudnetservice.cloudnet.v2.lib.process.ServerProcessData;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerProcessMeta;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Builder for a server process in the API for proxies and servers.
 * Uses {@link ServerProcessData} for storing the data.
 */
public class CoreServerProcessBuilder extends ServerProcessBuilder {

    /**
     * Creates a new server process builder for a server of the specified server group.
     * This value is mandatory as servers cannot be started without belonging to a group.
     *
     * @param serverGroupName the name of the server group that the server will be started from.
     *
     * @return the newly created server process builder.
     */
    public static ServerProcessBuilder create(String serverGroupName) {
        return new CoreServerProcessBuilder().serverGroupName(serverGroupName);
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
        try {
            final UUID uuid = UUID.randomUUID();
            this.getServerConfig().getProperties().append("cloudnet:requestId", uuid);

            ServerGroup serverGroup = CloudNet.getInstance().getServerGroup(this.getServerGroupName());
            if (this.getMemory() < serverGroup.getMemory()) {
                this.memory(serverGroup.getMemory());
            }

            if (serverGroup.getMaxOnlineServers() > 0 &&
                CloudNet.getInstance().getServersAndWaitings(this.getServerGroupName()).size() > serverGroup.getMaxOnlineServers()) {
                throw new IllegalStateException("Maximum amount of servers for this server group reached!");
            }

            Template template = this.getTemplate();
            if (template == null) {
                template = CloudNet.getInstance().getTemplateStatistics(serverGroup).entrySet().stream()
                                   .min(Map.Entry.comparingByValue()).orElseThrow(
                        () -> new IllegalStateException("Can't start a server without accompanying template!"))
                                   .getKey();
                this.template(template);
            }

            final String wrapperName = this.getWrapperName();
            Wrapper wrapper = wrapperName == null ? null : CloudNet.getInstance().getWrappers().get(wrapperName);
            if (wrapper == null) {
                wrapper = this.determineWrapper(serverGroup);
                this.wrapperName(wrapper.getName());
            }

            int serverPort = determineServerPort(wrapper);

            return CloudNet.getInstance().startServer(new ServerProcessMeta(this.getServerProcessData(),
                                                                            this.determineServiceId(wrapper),
                                                                            serverPort),
                                                      wrapper,
                                                      uuid);
        } catch (Exception exception) {
            final CompletableFuture<ServerProcessMeta> future = new CompletableFuture<>();
            future.completeExceptionally(exception);
            return future;
        }
    }

    private Wrapper determineWrapper(final ServerGroup serverGroup) {
        if (CloudNet.getInstance().getWrappers().isEmpty()) {
            throw new IllegalStateException("Can't start server without connected wrappers!");
        }

        Wrapper wrapper = null;
        int highestFreeMemory = 0;
        for (Wrapper wrapperInstance : CloudNet.getInstance().getWrappers().values()) {
            if (wrapperInstance.getChannel() != null &&
                wrapperInstance.getWrapperInfo() != null) {
                if (serverGroup.getWrapper().contains(wrapperInstance.getName())) {
                    int futureMemory = wrapperInstance.getUsedMemoryAndWaitings() + this.getMemory();
                    int freeMemory = wrapperInstance.getMaxMemory() - futureMemory;
                    if (wrapperInstance.getMaxMemory() > futureMemory &&
                        freeMemory > highestFreeMemory) {
                        wrapper = wrapperInstance;
                    }
                }
            }
        }
        return wrapper;
    }

    private static int determineServerPort(Wrapper wrapper) {
        List<Integer> boundPorts = wrapper.getBoundPorts();
        int startPort = wrapper.getWrapperInfo().getStartPort();
        while (boundPorts.contains(startPort)) {
            startPort += NetworkUtils.RANDOM.nextInt(1, 20);
        }
        return startPort;
    }

    private ServiceId determineServiceId(final Wrapper wrapper) {
        if (this.getServiceId() != null) {
            if (CloudNet.getInstance().getProxys().containsKey(this.getServiceId().getServerId()) ||
                CloudNet.getInstance().getServers().containsKey(this.getServiceId().getServerId())) {
                throw new IllegalStateException(String.format("A server with the ID %s is already running!",
                                                              this.getServiceId().getServerId()));
            }
            return this.getServiceId();
        }
        Collection<ServiceId> serviceIds = CloudNet.getInstance().getServerServiceIdsAndWaitings(this.getServerGroupName());
        List<Integer> usedIds = serviceIds.stream()
                                          .map(ServiceId::getId)
                                          .collect(Collectors.toList());

        int proxyId = 1;
        while (usedIds.contains(proxyId)) {
            proxyId++;
        }

        return new ServiceId(this.getServerGroupName(), proxyId, wrapper.getName());
    }

}
