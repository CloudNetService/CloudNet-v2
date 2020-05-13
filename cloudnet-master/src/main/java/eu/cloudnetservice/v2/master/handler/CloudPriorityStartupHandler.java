package eu.cloudnetservice.v2.master.handler;

import eu.cloudnetservice.v2.lib.server.ServerGroup;
import eu.cloudnetservice.v2.lib.server.ServerGroupMode;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.process.CoreServerProcessBuilder;

import java.util.Collection;

/**
 * Created by Tareko on 18.08.2017.
 */
public class CloudPriorityStartupHandler implements ICloudHandler {

    @Override
    public void onHandle(CloudNet cloudNet) {
        double onlineCount = CloudNet.getInstance().getNetworkManager().getOnlineCount();
        for (ServerGroup group : CloudNet.getInstance().getServerGroups().values()) {
            if (group.getPriorityService().getGlobal().getOnlineServers() == 0 ||
                group.getPriorityService().getGlobal().getOnlineCount() == 0 ||
                group.getGroupMode() == ServerGroupMode.STATIC ||
                group.isMaintenance()) {
                continue;
            }

            double priority = (group.getPriorityService().getGlobal().getOnlineServers() /
                ((double) group.getPriorityService().getGlobal().getOnlineCount())) *
                (onlineCount == 0 ? 1.0D : (onlineCount));
            Collection<String> servers = CloudNet.getInstance().getServersAndWaitings(group.getName());

            if (servers.size() == 0 && servers.size() < (priority <= 1 ? 1 : priority)) {
                CoreServerProcessBuilder.create(group.getName()).startServer();
                continue;
            }

            if (servers.size() < (priority <= 1 ? 1 : priority)) {
                //TODO Start with priority stop
                CoreServerProcessBuilder.create(group.getName()).startServer();
            }

        }
    }

    @Override
    public int getTicks() {
        return 50;
    }
}
