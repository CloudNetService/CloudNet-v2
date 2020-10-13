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

import eu.cloudnetservice.cloudnet.v2.api.CloudAPI;
import eu.cloudnetservice.cloudnet.v2.bridge.event.proxied.ProxiedSubChannelMessageEvent;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class BungeeCordChannel implements Listener {

    public void kickPlayer(UUID uuid) {
        CloudAPI.getInstance().sendCustomSubProxyMessage("ban-system",
                                                         "kick",
                                                         new Document("uuid", uuid).append("reason", "Du wurdest gekickt!"));
    }

    @EventHandler
    public void handleChannelIncomingMessage(ProxiedSubChannelMessageEvent e) {
        if (e.getChannel().equalsIgnoreCase("ban-system")) {
            if (e.getMessage().equalsIgnoreCase("kick")) {
                ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(e.getDocument().getObject("uuid", UUID.class));
                if (proxiedPlayer != null) {
                    proxiedPlayer.disconnect(TextComponent.fromLegacyText(e.getDocument().getString("reason")));
                }
            }
        }
    }
}
