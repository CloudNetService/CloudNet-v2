/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.dbsync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnetcore.CloudNet;

/**
 * Created by Tareko on 25.08.2017.
 */
public class PacketDBInSelectDatabase implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        CloudNet.getInstance().getDatabaseManager().getDatabase(packet.getData().getString("name"));
    }
}
