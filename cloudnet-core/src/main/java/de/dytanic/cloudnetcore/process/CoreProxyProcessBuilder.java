package de.dytanic.cloudnetcore.process;

import de.dytanic.cloudnet.lib.process.ProxyProcessBuilder;
import de.dytanic.cloudnet.lib.process.ProxyProcessData;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;

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
        final UUID uuid = UUID.randomUUID();
        this.getProxyProcessData().getProperties().append("cloudnet:requestId", uuid);
        Wrapper wrapper = CloudNet.getInstance().getWrappers().get(this.getWrapperName());
        if (wrapper == null) {
            wrapper = determineWrapper();
        }

        int proxyPort = determineProxyPort(wrapper);

        return CloudNet.getInstance().startProxy(new ProxyProcessMeta(this.getProxyProcessData(),
                                                                      this.determineServiceId(wrapper),
                                                                      proxyPort),
                                                 wrapper,
                                                 uuid);
    }

    private Wrapper determineWrapper() {
        if (CloudNet.getInstance().getWrappers().isEmpty()) {
            throw new IllegalStateException("Can't start proxy without connected wrappers!");
        }

        Wrapper wrapper = null;
        int highestFreeMemory = 0;
        for (Wrapper wrapperInstance : CloudNet.getInstance().getWrappers().values()) {
            if (wrapperInstance.getChannel() != null &&
                wrapperInstance.getWrapperInfo() != null) {
                int futureMemory = wrapperInstance.getUsedMemoryAndWaitings() + this.getMemory();
                int freeMemory = wrapperInstance.getMaxMemory() - futureMemory;
                if (wrapperInstance.getMaxMemory() > futureMemory &&
                    freeMemory > highestFreeMemory) {
                    wrapper = wrapperInstance;
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
