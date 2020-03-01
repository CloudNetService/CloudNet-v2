package de.dytanic.cloudnetcore.handler;

import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.process.CoreProxyProcessBuilder;
import de.dytanic.cloudnetcore.process.CoreServerProcessBuilder;

import java.util.Collection;

/**
 * Created by Tareko on 16.08.2017.
 */
public class CloudStartupHandler implements ICloudHandler {

    @Override
    public void onHandle(CloudNet cloudNet) {
        for (ServerGroup serverGroup : cloudNet.getServerGroups().values()) {
            Collection<String> servers = cloudNet.getServersAndWaitings(serverGroup.getName());
            if (servers.size() < serverGroup.getMinOnlineServers()
                && (serverGroup.getMaxOnlineServers() == -1
                || serverGroup.getMaxOnlineServers() > servers.size())) {
                CoreServerProcessBuilder.create(serverGroup.getName()).startServer();
            }
        }

        for (ProxyGroup proxyGroup : cloudNet.getProxyGroups().values()) {
            Collection<String> servers = cloudNet.getProxysAndWaitings(proxyGroup.getName());
            if (servers.size() < proxyGroup.getStartup()) {
                CoreProxyProcessBuilder.create(proxyGroup.getName()).startProxy();
            }
        }
    }

    @Override
    public int getTicks() {
        return 50;
    }
}
