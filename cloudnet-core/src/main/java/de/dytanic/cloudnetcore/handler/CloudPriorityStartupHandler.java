/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.handler;

import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

import java.util.Collection;

/**
 * Created by Tareko on 18.08.2017.
 */
public class CloudPriorityStartupHandler implements ICloudHandler {

    @Override
    public void onHandle(CloudNet cloudNet) {
        double onlineCount = CloudNet.getInstance().getNetworkManager().newCloudNetwork().getOnlineCount();
        for (ServerGroup group : CloudNet.getInstance().getServerGroups().values()) {
            if (group.getPriorityService().getGlobal().getOnlineServers() == 0 || group.getPriorityService()
                                                                                       .getGlobal()
                                                                                       .getOnlineCount() == 0 || group.getGroupMode() == ServerGroupMode.STATIC || group
                .isMaintenance()) {
                continue;
            }

            double priority = (group.getPriorityService().getGlobal().getOnlineServers() / ((double) group.getPriorityService()
                                                                                                          .getGlobal()
                                                                                                          .getOnlineCount())) * (onlineCount == 0 ? 1.0D : (onlineCount));
            Collection<String> servers = CloudNet.getInstance().getServersAndWaitings(group.getName());

            if (servers.size() == 0 && servers.size() < (priority <= 1 ? 1 : priority)) {
                CloudNet.getInstance().startGameServer(group);
                continue;
            }

            if (servers.size() < (priority <= 1 ? 1 : priority)) {
                CloudNet.getInstance().startGameServer(group, new Document(), true);
            }

        }
    }

    @Override
    public int getTicks() {
        return 50;
    }
}
