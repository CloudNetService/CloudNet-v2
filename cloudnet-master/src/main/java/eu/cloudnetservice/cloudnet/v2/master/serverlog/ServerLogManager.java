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

package eu.cloudnetservice.cloudnet.v2.master.serverlog;

import eu.cloudnetservice.cloudnet.v2.lib.MultiValue;
import eu.cloudnetservice.cloudnet.v2.lib.map.NetorHashMap;
import eu.cloudnetservice.cloudnet.v2.lib.server.screen.ScreenInfo;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ServerLogManager implements Runnable {

    private final NetorHashMap<String, MultiValue<String, Long>, Queue<ScreenInfo>> screenInfos = new NetorHashMap<>();

    public void append(String rnd, String serverId) {
        for (String key : screenInfos.keySet()) {
            if (this.screenInfos.getF(key).getFirst().equals(serverId)) {
                screenInfos.add(rnd,
                                new MultiValue<>(serverId, (System.currentTimeMillis() + 600000L)),
                                new ConcurrentLinkedQueue<>(this.screenInfos.getS(key)));
                return;
            }
        }

        MinecraftServer minecraftServer = CloudNet.getInstance().getServer(serverId);
        if (minecraftServer != null) {
            minecraftServer.getWrapper().enableScreen(minecraftServer.getServerInfo());
            screenInfos.add(rnd, new MultiValue<>(serverId, (System.currentTimeMillis() + 600000L)), new ConcurrentLinkedQueue<>());
            return;
        }

        ProxyServer proxyServer = CloudNet.getInstance().getProxy(serverId);
        if (proxyServer != null) {
            proxyServer.getWrapper().enableScreen(proxyServer.getProxyInfo());
            screenInfos.add(rnd, new MultiValue<>(serverId, (System.currentTimeMillis() + 600000L)), new ConcurrentLinkedQueue<>());
        }
    }

    public void appendScreenData(Collection<ScreenInfo> screenInfos) {
        if (screenInfos.size() != 0) {
            for (ScreenInfo screenInfo : screenInfos) {
                for (String key : this.screenInfos.keySet()) {
                    if (this.screenInfos.getF(key).getFirst().equalsIgnoreCase(screenInfo.getServiceId().getServerId())) {
                        this.screenInfos.getS(key).addAll(screenInfos);

                        while (this.screenInfos.getS(key).size() >= 64) {
                            this.screenInfos.getS(key).poll();
                        }
                    }
                }
            }
        }
    }

    public String dispatch(String rnd) {
        if (!this.screenInfos.contains(rnd)) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (ScreenInfo screenInfo : this.screenInfos.getS(rnd)) {
            stringBuilder.append("<p>").append(screenInfo.getLine()).append("</p>");
        }

        return stringBuilder.toString();
    }

    @Deprecated
    @Override
    public void run() {
        for (String key : screenInfos.keySet()) {
            if (screenInfos.getF(key).getSecond() < System.currentTimeMillis()) {
                String server = screenInfos.getF(key).getFirst();

                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(server);
                if (minecraftServer != null) {
                    minecraftServer.getWrapper().disableScreen(minecraftServer.getServerInfo());
                }

                ProxyServer proxyServer = CloudNet.getInstance().getProxy(server);
                if (proxyServer != null) {
                    proxyServer.getWrapper().disableScreen(proxyServer.getProxyInfo());
                }

                screenInfos.remove(key);
            }
        }
    }

    public NetorHashMap<String, MultiValue<String, Long>, Queue<ScreenInfo>> getScreenInfos() {
        return screenInfos;
    }
}
