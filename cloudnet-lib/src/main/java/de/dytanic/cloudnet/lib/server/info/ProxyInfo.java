package de.dytanic.cloudnet.lib.server.info;

import de.dytanic.cloudnet.lib.MultiValue;
import de.dytanic.cloudnet.lib.service.ServiceId;

import java.util.List;
import java.util.UUID;

/**
 * Created by Tareko on 25.05.2017.
 */
public class ProxyInfo {

    private ServiceId serviceId;

    private String host;
    private int port;
    private boolean online;
    private List<MultiValue<UUID, String>> players;
    private int memory;
    private int onlineCount;

    public ProxyInfo(ServiceId serviceId,
                     String host,
                     int port,
                     boolean online,
                     List<MultiValue<UUID, String>> players,
                     int memory,
                     int onlineCount) {
        this.serviceId = serviceId;
        this.host = host;
        this.port = port;
        this.online = online;
        this.players = players;
        this.memory = memory;
        this.onlineCount = onlineCount;
    }

    public int getMemory() {
        return memory;
    }

    @Override
    public String toString() {
        return "ProxyInfo{" + "serviceId=" + serviceId + ", host='" + host + '\'' + ", port=" + port + ", online=" + online + ", players=" + players + ", memory=" + memory + ", onlineCount=" + onlineCount + '}';
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getOnlineCount() {
        return onlineCount;
    }

    public boolean isOnline() {
        return online;
    }

    public List<MultiValue<UUID, String>> getPlayers() {
        return players;
    }

    public SimpleProxyInfo toSimple() {
        return new SimpleProxyInfo(serviceId, online, host, port, memory, onlineCount);
    }

}
