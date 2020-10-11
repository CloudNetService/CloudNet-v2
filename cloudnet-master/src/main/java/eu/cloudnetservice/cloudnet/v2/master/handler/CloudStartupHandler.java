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

import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.process.CoreProxyProcessBuilder;
import eu.cloudnetservice.cloudnet.v2.master.process.CoreServerProcessBuilder;

import java.util.Collection;

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
