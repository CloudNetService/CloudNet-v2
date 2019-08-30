/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.server.info;

import de.dytanic.cloudnet.lib.service.ServiceId;

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