/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.handler;

import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnetcore.CloudNet;

import java.util.Collection;

/**
 * Created by Tareko on 16.08.2017.
 */
public class CloudStartupHandler implements ICloudHandler {

    @Override
    public void onHandle(CloudNet cloudNet) {
        for (ServerGroup serverGroup : cloudNet.getServerGroups().values()) {
            Collection<String> servers = cloudNet.getServersAndWaitings(serverGroup.getName());
            if (servers.size() < serverGroup.getMinOnlineServers() && (serverGroup.getMaxOnlineServers() == -1 || serverGroup.getMaxOnlineServers() > servers
                .size())) {
                cloudNet.startGameServer(serverGroup);
            }
        }

        for (ProxyGroup serverGroup : cloudNet.getProxyGroups().values()) {
            Collection<String> servers = cloudNet.getProxysAndWaitings(serverGroup.getName());
            if (servers.size() < serverGroup.getStartup()) {
                cloudNet.startProxy(serverGroup);
            }
        }
    }

    @Override
    public int getTicks() {
        return 50;
    }
}
