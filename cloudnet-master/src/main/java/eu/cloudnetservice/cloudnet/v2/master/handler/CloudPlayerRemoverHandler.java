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

import eu.cloudnetservice.cloudnet.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CloudPlayerRemoverHandler implements ICloudHandler {

    @Override
    public void onHandle(CloudNet cloudNet) {
        Set<UUID> collection = CloudNet.getInstance().getProxys().values().stream()
                                       .map(ProxyServer::getProxyInfo)
                                       .map(ProxyInfo::getPlayers)
                                       .flatMap(maps -> maps.keySet().stream())
                                       .collect(Collectors.toSet());

        CloudNet.getInstance().getNetworkManager().getOnlinePlayers()
                .keySet()
                .removeIf(uuid -> !collection.contains(uuid));
    }

    @Override
    public int getTicks() {
        return 10;
    }
}
