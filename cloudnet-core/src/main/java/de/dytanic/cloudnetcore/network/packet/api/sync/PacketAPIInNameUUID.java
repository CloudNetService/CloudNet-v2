/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

import java.util.UUID;

/**
 * Created by Tareko on 20.08.2017.
 */
public class PacketAPIInNameUUID implements PacketAPIIO {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (packet.getData().contains("uniqueId")) {
            UUID uniqueId = packet.getData().getObject("uniqueId", UUID.class);
            String name = CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase().get(uniqueId);
            packetSender.sendPacket(getResult(packet, new Document("name", name)));
        } else {
            String name = packet.getData().getString("name");
            UUID uniqueId = CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase().get(name);
            packetSender.sendPacket(getResult(packet, new Document("uniqueId", uniqueId)));
        }
    }

    public Packet getResult(Packet packet, Document value) {
        return new Packet(packet.getUniqueId(), PacketRC.PLAYER_HANDLE, value);
    }
}
