package de.dytanic.cloudnet.lib.network;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.user.SimpledUser;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Tareko on 30.07.2017.
 */
public class WrapperExternal {

    public static final Type TYPE = TypeToken.get(WrapperExternal.class).getType();

    private CloudNetwork cloudNetwork;
    private SimpledUser user;
    private Map<String, ServerGroup> serverGroups;
    private Map<String, ProxyGroup> proxyGroups;

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
        return "de.dytanic.cloudnet.lib.network.WrapperExternal{" +
            "cloudNetwork=" + cloudNetwork +
            ", user=" + user +
            ", serverGroups=" + serverGroups +
            ", proxyGroups=" + proxyGroups +
            '}';
    }
}
