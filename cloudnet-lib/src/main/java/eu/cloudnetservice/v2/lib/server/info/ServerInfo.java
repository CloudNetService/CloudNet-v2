package eu.cloudnetservice.v2.lib.server.info;

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.v2.lib.server.ServerConfig;
import eu.cloudnetservice.v2.lib.server.ServerState;
import eu.cloudnetservice.v2.lib.server.template.Template;
import eu.cloudnetservice.v2.lib.service.ServiceId;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * Created by Tareko on 24.05.2017.
 */
public class ServerInfo {
    public static final Type TYPE = TypeToken.get(ServerInfo.class).getType();

    private ServiceId serviceId;
    private String host;
    private int port;
    private boolean online;
    private List<String> players;
    private int memory;
    private String motd;
    private int onlineCount;
    private int maxPlayers;
    private ServerState serverState;
    private ServerConfig serverConfig;
    private Template template;
    public ServerInfo(ServiceId serviceId,
                      String host,
                      int port,
                      boolean online,
                      List<String> players,
                      int memory,
                      String motd,
                      int onlineCount,
                      int maxPlayers,
                      ServerState serverState,
                      ServerConfig serverConfig,
                      Template template) {
        this.serviceId = serviceId;
        this.host = host;
        this.port = port;
        this.online = online;
        this.players = players;
        this.memory = memory;
        this.motd = motd;
        this.onlineCount = onlineCount;
        this.maxPlayers = maxPlayers;
        this.serverState = serverState;
        this.serverConfig = serverConfig;
        this.template = template;
    }

    @Override
    public int hashCode() {
        int result = serviceId != null ? serviceId.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + port;
        result = 31 * result + (online ? 1 : 0);
        result = 31 * result + (players != null ? players.hashCode() : 0);
        result = 31 * result + memory;
        result = 31 * result + (motd != null ? motd.hashCode() : 0);
        result = 31 * result + onlineCount;
        result = 31 * result + maxPlayers;
        result = 31 * result + (serverState != null ? serverState.hashCode() : 0);
        result = 31 * result + (serverConfig != null ? serverConfig.hashCode() : 0);
        result = 31 * result + (template != null ? template.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ServerInfo that = (ServerInfo) o;

        if (port != that.port) {
            return false;
        }
        if (online != that.online) {
            return false;
        }
        if (memory != that.memory) {
            return false;
        }
        if (onlineCount != that.onlineCount) {
            return false;
        }
        if (maxPlayers != that.maxPlayers) {
            return false;
        }
        if (!Objects.equals(serviceId, that.serviceId)) {
            return false;
        }
        if (!Objects.equals(host, that.host)) {
            return false;
        }
        if (!Objects.equals(players, that.players)) {
            return false;
        }
        if (!Objects.equals(motd, that.motd)) {
            return false;
        }
        if (serverState != that.serverState) {
            return false;
        }
        if (!Objects.equals(serverConfig, that.serverConfig)) {
            return false;
        }
        return Objects.equals(template, that.template);
    }

    @Override
    public String toString() {
        return "ServerInfo{" + "serviceId=" + serviceId + ", host='" + host + '\'' + ", port=" + port + ", online=" + online + ", players=" + players + ", memory=" + memory + ", motd='" + motd + '\'' + ", onlineCount=" + onlineCount + ", maxPlayers=" + maxPlayers + ", serverState=" + serverState + ", serverConfig=" + serverConfig + ", template=" + template + '}';
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public int getOnlineCount() {
        return onlineCount;
    }

    public int getPort() {
        return port;
    }

    public int getMemory() {
        return memory;
    }

    public Template getTemplate() {
        return template;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<String> getPlayers() {
        return players;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public ServerState getServerState() {
        return serverState;
    }

    public String getHost() {
        return host;
    }

    public String getMotd() {
        return motd;
    }

    public boolean isOnline() {
        return online;
    }

    public boolean isIngame() {

        if (serverState == null) {
            serverState = ServerState.LOBBY;
        }

        if (motd == null) {
            motd = "null";
        }

        return serverState == ServerState.INGAME || (motd.equalsIgnoreCase("INGAME") || motd.equalsIgnoreCase("RUNNING"));
    }

    public SimpleServerInfo toSimple() {
        return new SimpleServerInfo(serviceId, host, port, onlineCount, maxPlayers);
    }

}
