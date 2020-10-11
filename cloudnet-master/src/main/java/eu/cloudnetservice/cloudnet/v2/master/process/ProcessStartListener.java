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

import eu.cloudnetservice.cloudnet.v2.event.EventListener;
import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyProcessMeta;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerProcessMeta;
import eu.cloudnetservice.cloudnet.v2.master.api.event.network.ChannelInitEvent;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class ProcessStartListener implements EventListener<ChannelInitEvent> {

    private final Map<UUID, CompletableFuture<ProxyProcessMeta>> waitingProxies = new ConcurrentHashMap<>();
    private final Map<UUID, CompletableFuture<ServerProcessMeta>> waitingServers = new ConcurrentHashMap<>();

    @Override
    public void onCall(final ChannelInitEvent event) {
        if (event.getINetworkComponent() instanceof ProxyServer) {
            final ProxyServer proxyServer = (ProxyServer) event.getINetworkComponent();
            UUID uuid = proxyServer.getProcessMeta().getProperties().getObject("cloudnet:requestId", UUID.class);
            this.waitingProxies.remove(uuid).complete(proxyServer.getProcessMeta());
        } else if (event.getINetworkComponent() instanceof MinecraftServer) {
            final MinecraftServer proxyServer = (MinecraftServer) event.getINetworkComponent();
            UUID uuid = proxyServer.getProcessMeta().getServerConfig().getProperties().getObject("cloudnet:requestId", UUID.class);
            this.waitingServers.remove(uuid).complete(proxyServer.getProcessMeta());
        }
    }

    public CompletableFuture<ProxyProcessMeta> waitForProxy(UUID uuid) {
        CompletableFuture<ProxyProcessMeta> future = new CompletableFuture<>();
        this.waitingProxies.put(uuid, future);
        return future;
    }

    public CompletableFuture<ServerProcessMeta> waitForServer(UUID uuid) {
        CompletableFuture<ServerProcessMeta> future = new CompletableFuture<>();
        this.waitingServers.put(uuid, future);
        return future;
    }
}
