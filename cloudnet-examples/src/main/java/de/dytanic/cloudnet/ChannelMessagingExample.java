/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.network.packet.out.PacketOutCustomChannelMessage;
import de.dytanic.cloudnet.bridge.event.proxied.ProxiedSubChannelMessageEvent;
import de.dytanic.cloudnet.lib.utility.document.Document;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Tareko on 15.10.2017.
 */
public class ChannelMessagingExample {

    public void sendCustomMessage() {
        CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutCustomChannelMessage("some-sub-channel-for-proxy",
                                                                                                   "handle",
                                                                                                   new Document("foo",
                                                                                                                "bar"))); //send a custom channel message to all
        CloudAPI.getInstance().sendCustomSubProxyMessage("some-sub-channel-for-proxy",
                                                         "handle",
                                                         new Document("foo", "bar")); //send a custom channel message to all proxys
    }

    @EventHandler
    public void channelSubReceive(ProxiedSubChannelMessageEvent e) //handle the received channel message
    {
        if (e.getChannel().equalsIgnoreCase("some-sub-channel-for-proxy")) {
            if (e.getMessage().equalsIgnoreCase("handle")) {
                System.out.println(e.getDocument().convertToJson());
            }
        }
    }

}
