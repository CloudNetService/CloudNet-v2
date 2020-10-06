package eu.cloudnetservice.cloudnet.v2.lib.server.info;

import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;

import java.net.InetAddress;

public class SimpleServerInfo {

    private final ServiceId serviceId;

    private final InetAddress hostAddress;

    private final int port;

    private final int onlineCount;

    private final int maxPlayers;

    public SimpleServerInfo(ServiceId serviceId, InetAddress hostAddress, int port, int onlineCount, int maxPlayers) {
        this.serviceId = serviceId;
        this.hostAddress = hostAddress;
        this.port = port;
        this.onlineCount = onlineCount;
        this.maxPlayers = maxPlayers;
    }

    public int getPort() {
        return port;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public int getOnlineCount() {
        return onlineCount;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public InetAddress getHostAddress() {
        return hostAddress;
    }
}