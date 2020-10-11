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

import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;

public class CloudStopCheckHandler implements ICloudHandler {

    @Override
    public void onHandle(CloudNet cloudNet) {
        for (MinecraftServer minecraftServer : cloudNet.getServers().values()) {
            if (minecraftServer.getChannelLostTime() != 0L && minecraftServer.getChannel() == null) {
                if ((minecraftServer.getChannelLostTime() + 5000L) < System.currentTimeMillis()) {
                    minecraftServer.getWrapper().stopServer(minecraftServer);
                }
            }
        }

        for (ProxyServer proxyServer : cloudNet.getProxys().values()) {
            if (proxyServer.getChannelLostTime() != 0L && proxyServer.getChannel() == null) {
                if ((proxyServer.getChannelLostTime() + 5000L) < System.currentTimeMillis()) {
                    proxyServer.getWrapper().stopProxy(proxyServer);
                }
            }
        }
    }

    @Override
    public int getTicks() {
        return 100;
    }
}
