/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

public final class PacketInStartProxy implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        ProxyProcessMeta proxyProcessMeta = packet.getData().getObject("proxyProcess", ProxyProcessMeta.TYPE);

        if (!packet.getData().contains("async")) {
            System.out.println("Proxy process is now in queue [" + proxyProcessMeta.getServiceId() + ']');
            CloudNetWrapper.getInstance().getServerProcessQueue().putProcess(proxyProcessMeta);
        } else {
            CloudNetWrapper.getInstance().getServerProcessQueue().patchAsync(proxyProcessMeta);
        }
    }
}
