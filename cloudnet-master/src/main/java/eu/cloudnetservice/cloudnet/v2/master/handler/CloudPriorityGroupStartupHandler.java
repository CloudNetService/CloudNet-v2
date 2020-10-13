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

package eu.cloudnetservice.cloudnet.v2.master.handler;

import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroupMode;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.process.CoreServerProcessBuilder;

import java.util.Collection;

public class CloudPriorityGroupStartupHandler implements ICloudHandler {

    @Override
    public void onHandle(CloudNet cloudNet) {
        for (ServerGroup group : CloudNet.getInstance().getServerGroups().values()) {
            double onlineCount = CloudNet.getInstance().getOnlineCount(group.getName());
            if (group.getPriorityService().getGroup().getOnlineServers() == 0 || group.getPriorityService()
                                                                                      .getGlobal()
                                                                                      .getOnlineCount() == 0 || group.getGroupMode() == ServerGroupMode.STATIC || group
                .isMaintenance()) {
                continue;
            }

            double priority = (group.getPriorityService().getGroup().getOnlineServers() / ((double) group.getPriorityService()
                                                                                                         .getGroup()
                                                                                                         .getOnlineCount())) * (onlineCount == 0 ? 1.0D : (onlineCount));
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
