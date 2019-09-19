package de.dytanic.cloudnet.lib.server.info;

import de.dytanic.cloudnet.lib.service.ServiceId;

/**
 * Created by Tareko on 02.07.2017.
 */
public class SimpleProxyInfo {

    private ServiceId serviceId;
    private boolean online;
    private String hostName;
    private int port;
    private int memory;
    private int onlineCount;

    public SimpleProxyInfo(ServiceId serviceId, boolean online, String hostName, int port, int memory, int onlineCount) {
        this.serviceId = serviceId;
        this.online = online;
        this.hostName = hostName;
        this.port = port;
        this.memory = memory;
        this.onlineCount = onlineCount;
    }

    public int getOnlineCount() {
        return onlineCount;
    }

    public int getPort() {
        return port;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public int getMemory() {
        return memory;
    }

    public String getHostName() {
        return hostName;
    }

    public boolean isOnline() {
        return online;
    }
}
