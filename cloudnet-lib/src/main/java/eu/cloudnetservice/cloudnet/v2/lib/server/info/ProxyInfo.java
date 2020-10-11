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
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ProxyInfo {

    public static final Type TYPE = TypeToken.get(ProxyInfo.class).getType();

    private final ServiceId serviceId;

    private final InetAddress host;
    private final boolean online;
    private final Map<UUID, String> players;
    private final int memory;
    private final int port;

    public ProxyInfo(ServiceId serviceId,
                     InetAddress host,
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

    public InetAddress getHost() {
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
