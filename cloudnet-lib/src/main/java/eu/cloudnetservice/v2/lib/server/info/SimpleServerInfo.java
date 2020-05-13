package eu.cloudnetservice.v2.lib.server.info;

import eu.cloudnetservice.v2.lib.service.ServiceId;

public class SimpleServerInfo {

    private ServiceId serviceId;

    private String hostAddress;

    private int port;

    private int onlineCount;

    private int maxPlayers;

    public SimpleServerInfo(ServiceId serviceId, String hostAddress, int port, int onlineCount, int maxPlayers) {
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

    public String getHostAddress() {
        return hostAddress;
    }
}