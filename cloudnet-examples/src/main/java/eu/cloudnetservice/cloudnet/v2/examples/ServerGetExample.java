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

package eu.cloudnetservice.cloudnet.v2.examples;

import eu.cloudnetservice.cloudnet.v2.api.builders.ApiServerProcessBuilder;
import eu.cloudnetservice.cloudnet.v2.bridge.event.bukkit.BukkitServerAddEvent;
import eu.cloudnetservice.cloudnet.v2.bridge.event.proxied.ProxiedServerAddEvent;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerConfig;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import net.md_5.bungee.api.plugin.Listener;
import org.bukkit.event.EventHandler;

public class ServerGetExample implements Listener, org.bukkit.event.Listener {

    @net.md_5.bungee.event.EventHandler
    public void handle(ProxiedServerAddEvent e) {
        if (e.getServerInfo().getServerConfig().getProperties().contains("myUUID") && e.getServerInfo()
                                                                                       .getServerConfig()
                                                                                       .getProperties()
                                                                                       .getString("myUUID")
                                                                                       .equals("test")) {
            ServerInfo serverInfo = e.getServerInfo();
            /* ... */
        }

        if (e.getServerInfo().getServiceId().getServerId().equals("Lobby-1")) {
            ServerInfo serverInfo = e.getServerInfo();
            /* ... */
        }
    }

    @EventHandler
    public void handle(BukkitServerAddEvent e) {
        /* - // - */
    }

    public void start() {
        ApiServerProcessBuilder.create("Lobby")
                               .serverConfig(new ServerConfig(true, new Document("myUUID", "test"), System.currentTimeMillis()))
                               .startServer();
    }

}
