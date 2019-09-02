/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.CloudServer;
import de.dytanic.cloudnetcore.network.components.INetworkComponent;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.util.ChannelFilter;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutCustomSubChannelMessage;

/**
 * Created by Tareko on 24.08.2017.
 */
public class PacketInCustomSubChannelMessage extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        DefaultType defaultType = data.getObject("defaultType", DefaultType.class);
        String channel = data.getString("channel");
        String message = data.getString("message");
        Document document = data.getDocument("value");
        if (defaultType.equals(DefaultType.BUKKIT)) {
            if (data.contains("serverId")) {
                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(data.getString("serverId"));
                if (minecraftServer != null) {
                    minecraftServer.sendPacket(new PacketOutCustomSubChannelMessage(channel, message, document));
                    return;
                }

                CloudServer cloudServer = CloudNet.getInstance().getCloudGameServer(data.getString("serverId"));
                if (cloudServer != null) {
                    cloudServer.sendPacket(new PacketOutCustomSubChannelMessage(channel, message, document));
                }
            } else {
                CloudNet.getInstance().getNetworkManager().sendAll(new PacketOutCustomSubChannelMessage(channel, message, document),
                                                                   new ChannelFilter() {
                                                                       @Override
                                                                       public boolean accept(INetworkComponent networkComponent) {
                                                                           return networkComponent instanceof MinecraftServer || networkComponent instanceof CloudServer;
                                                                       }
                                                                   });
            }
        } else {
            if (data.contains("serverId")) {
                ProxyServer proxyServer = CloudNet.getInstance().getProxy(data.getString("serverId"));
                if (proxyServer != null) {
                    proxyServer.sendPacket(new PacketOutCustomSubChannelMessage(channel, message, document));
                }
            } else {
                CloudNet.getInstance().getNetworkManager().sendToProxy(new PacketOutCustomSubChannelMessage(channel, message, document));
            }
        }
    }
}
