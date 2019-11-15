/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.ProxyServer;

/**
 * Created by Tareko on 23.07.2017.
 */
public class PacketInUpdateProxyInfo implements PacketInHandler {


    public void handleInput(Packet packet, PacketSender packetSender) {
        if (packetSender instanceof ProxyServer) {
            ProxyServer proxyServer = (ProxyServer) packetSender;
            proxyServer.setLastProxyInfo(proxyServer.getProxyInfo());
            proxyServer.setProxyInfo(packet.getData().getObject("proxyInfo", ProxyInfo.TYPE));
            CloudNet.getInstance().getNetworkManager().handleProxyInfoUpdate(
                proxyServer, packet.getData().getObject("proxyInfo", ProxyInfo.TYPE));
        }
    }
}
