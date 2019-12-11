/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnetcore.CloudNet;

/**
 * Created by Tareko on 25.08.2017.
 */
public class PacketInDisableScreen implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        ServiceId serviceId = packet.getData().getObject("serviceId", ServiceId.TYPE);
        CloudNet.getInstance().getScreenProvider().handleDisableScreen(serviceId);
    }
}
