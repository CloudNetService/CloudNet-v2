/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api;

import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.ProxyServer;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInStopProxy extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        String proxyId = data.getString("serverId");
        ProxyServer proxyServer = CloudNet.getInstance().getProxy(proxyId);
        if (proxyServer != null) {
            proxyServer.getWrapper().stopProxy(proxyServer);
        }
    }
}
