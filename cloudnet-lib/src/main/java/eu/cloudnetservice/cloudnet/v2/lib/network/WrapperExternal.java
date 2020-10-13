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

package eu.cloudnetservice.cloudnet.v2.lib.network;

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.cloudnet.v2.lib.CloudNetwork;
import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.lib.user.SimpledUser;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

public class WrapperExternal {

    public static final Type TYPE = TypeToken.get(WrapperExternal.class).getType();

    private final CloudNetwork cloudNetwork;
    private final SimpledUser user;
    private final Map<String, ServerGroup> serverGroups;
    private final Map<String, ProxyGroup> proxyGroups;

    public WrapperExternal(CloudNetwork cloudNetwork,
                           SimpledUser user,
                           Map<String, ServerGroup> serverGroups,
                           Map<String, ProxyGroup> proxyGroups) {
        this.cloudNetwork = cloudNetwork;
        this.user = user;
        this.serverGroups = serverGroups;
        this.proxyGroups = proxyGroups;
    }

    public Map<String, ProxyGroup> getProxyGroups() {
        return proxyGroups;
    }

    public Map<String, ServerGroup> getServerGroups() {
        return serverGroups;
    }

    public CloudNetwork getCloudNetwork() {
        return cloudNetwork;
    }

    public SimpledUser getUser() {
        return user;
    }

    @Override
    public int hashCode() {
        int result = cloudNetwork != null ? cloudNetwork.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (serverGroups != null ? serverGroups.hashCode() : 0);
        result = 31 * result + (proxyGroups != null ? proxyGroups.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WrapperExternal)) {
            return false;
        }

        final WrapperExternal that = (WrapperExternal) o;

        if (!Objects.equals(cloudNetwork, that.cloudNetwork)) {
            return false;
        }
        if (!Objects.equals(user, that.user)) {
            return false;
        }
        if (!Objects.equals(serverGroups, that.serverGroups)) {
            return false;
        }
        return Objects.equals(proxyGroups, that.proxyGroups);
    }

    @Override
    public String toString() {
        return "WrapperExternal{" +
            "cloudNetwork=" + cloudNetwork +
            ", user=" + user +
            ", serverGroups=" + serverGroups +
            ", proxyGroups=" + proxyGroups +
            '}';
    }
}
