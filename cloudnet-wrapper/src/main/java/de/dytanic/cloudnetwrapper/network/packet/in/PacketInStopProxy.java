/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

public class PacketInStopProxy implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        ProxyInfo serverInfo = packet.getData().getObject("proxyInfo", ProxyInfo.TYPE);
        if (CloudNetWrapper.getInstance().getProxys().containsKey(serverInfo.getServiceId().getServerId())) {
            CloudNetWrapper.getInstance().getProxys().get(serverInfo.getServiceId().getServerId()).shutdown();
        }
    }
}
