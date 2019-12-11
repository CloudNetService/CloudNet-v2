/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import de.dytanic.cloudnet.lib.cloudserver.CloudServerMeta;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

/**
 * Created by Tareko on 22.10.2017.
 */
public class PacketInStartCloudServer implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        CloudServerMeta cloudServerMeta = packet.getData().getObject("cloudServerMeta", CloudServerMeta.TYPE);

        if (!packet.getData().contains("async")) {
            System.out.println("Cloud game server process is now in queue [" + cloudServerMeta.getServiceId() + ']');
            CloudNetWrapper.getInstance().getServerProcessQueue().putProcess(cloudServerMeta);
        } else {
            CloudNetWrapper.getInstance().getServerProcessQueue().patchAsync(cloudServerMeta);
        }
    }
}
