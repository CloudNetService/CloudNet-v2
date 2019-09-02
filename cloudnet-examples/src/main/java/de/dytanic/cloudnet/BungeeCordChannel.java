/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.event.proxied.ProxiedSubChannelMessageEvent;
import de.dytanic.cloudnet.lib.utility.document.Document;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

/**
 * Created by Tareko on 19.10.2017.
 */
public class BungeeCordChannel {

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
                    proxiedPlayer.disconnect(e.getDocument().getString("reason"));
                }
            }
        }
    }
}
