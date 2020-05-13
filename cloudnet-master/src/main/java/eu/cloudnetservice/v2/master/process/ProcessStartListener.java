package eu.cloudnetservice.v2.master.process;

import de.dytanic.cloudnet.event.EventListener;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.server.ServerProcessMeta;
import eu.cloudnetservice.v2.master.api.event.network.ChannelInitEvent;
import eu.cloudnetservice.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.v2.master.network.components.ProxyServer;

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
