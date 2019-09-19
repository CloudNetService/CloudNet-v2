/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.dbsync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.packet.api.sync.PacketAPIIO;

/**
 * Created by Tareko on 25.08.2017.
 */
public class PacketDBInExistsDocument extends PacketAPIIO {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        String name = data.getString("name");
        boolean document = CloudNet.getInstance().getDatabaseManager().getDatabase(data.getString("db")).containsDoc(name);
        packetSender.sendPacket(getResult(new Document("exists", document)));
    }

    @Override
    protected Packet getResult(Document value) {
        return new Packet(packetUniqueId, PacketRC.DB, value);
    }
}
