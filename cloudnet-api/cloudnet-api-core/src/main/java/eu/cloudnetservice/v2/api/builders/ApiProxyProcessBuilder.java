package eu.cloudnetservice.v2.api.builders;

import eu.cloudnetservice.v2.api.CloudAPI;
import eu.cloudnetservice.v2.api.network.packet.api.PacketOutStartProxy;
import eu.cloudnetservice.v2.lib.process.ProxyProcessBuilder;
import eu.cloudnetservice.v2.lib.process.ProxyProcessData;
import eu.cloudnetservice.v2.lib.server.ProxyProcessMeta;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Builder for a proxy process in the API for proxies and servers.
 * Uses {@link ProxyProcessData} for storing the data.
 */
public class ApiProxyProcessBuilder extends ProxyProcessBuilder {

    /**
     * Creates a new proxy process builder for a proxy of the specified proxy group.
     * This value is mandatory as proxies cannot be started without belonging to a group.
     *
     * @param proxyGroupName the name of the proxy group that the proxy will be started from.
     *
     * @return the newly created proxy process builder.
     */
    public static ProxyProcessBuilder create(String proxyGroupName) {
        return new ApiProxyProcessBuilder().proxyGroupName(proxyGroupName);
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
        final UUID uuid = UUID.randomUUID();
        this.getProxyProcessData().getProperties().append("cloudnet:requestId", uuid);
        CloudAPI.getInstance().getNetworkConnection().sendAsynchronous(
            new PacketOutStartProxy(this.getProxyProcessData())
        );
        return CloudAPI.getInstance().getCloudService().waitForProxy(uuid);
    }

}
