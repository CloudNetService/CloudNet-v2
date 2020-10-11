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

package eu.cloudnetservice.cloudnet.v2.bridge.event.bukkit;

import eu.cloudnetservice.cloudnet.v2.lib.server.info.ProxyInfo;
import org.bukkit.event.HandlerList;

/**
 * This event is called whenever a proxy has been removed from the CloudNet network.
 * The proxy is <b>not</b> connected to the network anymore.
 */
public class BukkitProxyRemoveEvent extends BukkitCloudEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final ProxyInfo serverInfo;

    public BukkitProxyRemoveEvent(ProxyInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * The proxy information about the proxy that has been removed from the network.
     *
     * @return the proxy information about the removed proxy.
     */
    public ProxyInfo getProxyInfo() {
        return serverInfo;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
