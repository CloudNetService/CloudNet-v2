package de.dytanic.cloudnet.lib.network;

import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.user.SimpledUser;

import java.util.Map;

/**
 * Created by Tareko on 30.07.2017.
 */
public class WrapperExternal {

    private CloudNetwork cloudNetwork;

    private SimpledUser user;

    private java.util.Map<String, ServerGroup> serverGroups;

    private java.util.Map<String, ProxyGroup> proxyGroups;

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
}
