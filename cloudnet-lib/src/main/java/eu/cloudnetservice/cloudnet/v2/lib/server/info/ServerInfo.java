/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.lib.server.info;

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerConfig;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerState;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ServerInfo {
    public static final Type TYPE = TypeToken.get(ServerInfo.class).getType();

    private final ServiceId serviceId;
    private final InetAddress host;
    private final boolean online;
    private final List<String> players;
    private final int port;
    private final int memory;
    private final int maxPlayers;
    private final ServerConfig serverConfig;
    private final Template template;
    private String motd;
    private ServerState serverState;

    public ServerInfo(ServiceId serviceId,
                      InetAddress host,
                      int port,
                      boolean online,
                      List<String> players,
                      int memory,
                      String motd,
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
        this.maxPlayers = maxPlayers;
        this.serverState = serverState;
        this.serverConfig = serverConfig;
        this.template = template;
    }

    public void fetch(Consumer<ServerInfo> serverInfo) {
        serverInfo.accept(this);
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
        return "ServerInfo{" +
            "serviceId=" + serviceId +
            ", host='" + host + '\'' +
            ", port=" + port +
            ", online=" + online +
            ", players=" + players +
            ", memory=" + memory +
            ", maxPlayers=" + maxPlayers +
            ", serverConfig=" + serverConfig +
            ", template=" + template +
            ", motd='" + motd + '\'' +
            ", serverState=" + serverState +
            '}';
    }

    public int getMemory() {
        return memory;
    }

    public Template getTemplate() {
        return template;
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
        return new SimpleServerInfo(getServiceId(), getHost(), getPort(), getOnlineCount(), getMaxPlayers());
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public InetAddress getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getOnlineCount() {
        return players != null ? players.size() : 0;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

}
