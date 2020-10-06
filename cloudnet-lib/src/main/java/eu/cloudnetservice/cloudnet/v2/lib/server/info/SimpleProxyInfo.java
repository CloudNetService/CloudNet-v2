package eu.cloudnetservice.cloudnet.v2.lib.server.info;

import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;

import java.net.InetAddress;

/**
 * Created by Tareko on 02.07.2017.
 */
public class SimpleProxyInfo {

    private final ServiceId serviceId;
    private final boolean online;
    private final InetAddress hostName;
    private final int port;
    private final int memory;
    private final int onlineCount;

    public SimpleProxyInfo(ServiceId serviceId, boolean online, InetAddress hostName, int port, int memory, int onlineCount) {
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

    public InetAddress getHostName() {
        return hostName;
    }

    public boolean isOnline() {
        return online;
    }
}
