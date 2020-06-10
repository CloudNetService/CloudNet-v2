package eu.cloudnetservice.cloudnet.v2.lib.server.info;

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by Tareko on 25.05.2017.
 */
public class ProxyInfo {

    public static final Type TYPE = TypeToken.get(ProxyInfo.class).getType();

    private final ServiceId serviceId;

    private final String host;
    private final boolean online;
    private final Map<UUID, String> players;
    private final int memory;
    private final int port;

    public ProxyInfo(ServiceId serviceId,
                     String host,
                     int port,
                     boolean online,
                     Map<UUID, String> players,
                     int memory) {
        this.serviceId = serviceId;
        this.host = host;
        this.port = port;
        this.online = online;
        this.players = players;
        this.memory = memory;
    }

    public void fetch(Consumer<ProxyInfo> proxyInfo) {
        proxyInfo.accept(this);
    }

    @Override
    public String toString() {
        return "ProxyInfo{" +
            "serviceId=" + serviceId +
            ", host='" + host + '\'' +
            ", port=" + port +
            ", online=" + online +
            ", players=" + players +
            ", memory=" + memory +
            '}';
    }

    public Map<UUID, String> getPlayers() {
        return players;
    }

    public SimpleProxyInfo toSimple() {
        return new SimpleProxyInfo(getServiceId(), isOnline(), getHost(), getPort(), getMemory(), getOnlineCount());
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public boolean isOnline() {
        return online;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getMemory() {
        return memory;
    }

    public int getOnlineCount() {
        return players != null ? players.size() : 0;
    }

}
