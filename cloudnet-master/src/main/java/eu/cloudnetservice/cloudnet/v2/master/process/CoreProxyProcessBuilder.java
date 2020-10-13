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

import eu.cloudnetservice.cloudnet.v2.lib.process.ProxyProcessBuilder;
import eu.cloudnetservice.cloudnet.v2.lib.process.ProxyProcessData;
import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyProcessMeta;
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Builder for a proxy process in the API for proxies and servers.
 * Uses {@link ProxyProcessData} for storing the data.
 */
public class CoreProxyProcessBuilder extends ProxyProcessBuilder {

    /**
     * Creates a new proxy process builder for a proxy of the specified proxy group.
     * This value is mandatory as proxies cannot be started without belonging to a group.
     *
     * @param proxyGroupName the name of the proxy group that the proxy will be started from.
     *
     * @return the newly created proxy process builder.
     */
    public static ProxyProcessBuilder create(String proxyGroupName) {
        return new CoreProxyProcessBuilder().proxyGroupName(proxyGroupName);
    }

    /**
     * Requests the master to initiate this proxy startup.
     * This will trigger a sequence of messages to be passed between the calling service,
     * the master and the wrapper that will start the proxy.
     * <p>
     * Once the proxy is started, the returned future will be completed and contains the
     * process metadata that was valid at the time of starting up.
     * <p>
     * Note that the delay between requesting the start (ie. this method) and the actual
     * completion of the returned future is indefinite and the future may not actually
     * be completed at all.
     *
     * @return a future that will be completed once the proxy is connected to the cloud network.
     */
    public CompletableFuture<ProxyProcessMeta> startProxy() {
        try {
            final UUID uuid = UUID.randomUUID();
            this.getProxyProcessData().getProperties().append("cloudnet:requestId", uuid);

            final ProxyGroup proxyGroup = CloudNet.getInstance().getProxyGroup(this.getProxyGroupName());
            if (this.getMemory() < proxyGroup.getMemory()) {
                this.memory(proxyGroup.getMemory());
            }

            final String wrapperName = this.getWrapperName();
            Wrapper wrapper = wrapperName == null ? null : CloudNet.getInstance().getWrappers().get(wrapperName);
            if (wrapper == null) {
                wrapper = this.determineWrapper(proxyGroup);
                this.wrapperName(wrapper.getName());
            }

            int proxyPort = this.determineProxyPort(wrapper);

            return CloudNet.getInstance().startProxy(new ProxyProcessMeta(this.getProxyProcessData(),
                                                                          this.determineServiceId(wrapper),
                                                                          proxyPort),
                                                     wrapper,
                                                     uuid);
        } catch (Exception exception) {
            final CompletableFuture<ProxyProcessMeta> future = new CompletableFuture<>();
            future.completeExceptionally(exception);
            return future;
        }
    }

    private Wrapper determineWrapper(final ProxyGroup proxyGroup) {
        if (CloudNet.getInstance().getWrappers().isEmpty()) {
            throw new IllegalStateException("Can't start proxy without connected wrappers!");
        }

        Wrapper wrapper = null;
        int highestFreeMemory = 0;
        for (Wrapper wrapperInstance : CloudNet.getInstance().getWrappers().values()) {
            if (wrapperInstance.getChannel() != null &&
                wrapperInstance.getWrapperInfo() != null) {
                if (proxyGroup.getWrapper().contains(wrapperInstance.getName())) {
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

    private int determineProxyPort(final Wrapper wrapper) {
        List<Integer> boundPorts = wrapper.getBoundPorts();
        ProxyGroup proxyGroup = CloudNet.getInstance().getProxyGroup(this.getProxyGroupName());
        int startPort = proxyGroup.getStartPort();
        while (boundPorts.contains(startPort)) {
            startPort++;
        }
        return startPort;
    }

    private ServiceId determineServiceId(final Wrapper wrapper) {
        if (this.getServiceId() != null) {
            if (CloudNet.getInstance().getProxys().containsKey(this.getServiceId().getServerId()) ||
                CloudNet.getInstance().getServers().containsKey(this.getServiceId().getServerId())) {
                throw new IllegalStateException(String.format("A proxy with the ID %s is already running!",
                                                              this.getServiceId().getServerId()));
            }
            return this.getServiceId();
        }
        Collection<ServiceId> serviceIds = CloudNet.getInstance().getProxyServiceIdsAndWaitingServices(this.getProxyGroupName());
        List<Integer> usedIds = serviceIds.stream()
                                          .map(ServiceId::getId)
                                          .collect(Collectors.toList());

        int proxyId = 1;
        while (usedIds.contains(proxyId)) {
            proxyId++;
        }

        return new ServiceId(this.getProxyGroupName(), proxyId, wrapper.getName());
    }

}
