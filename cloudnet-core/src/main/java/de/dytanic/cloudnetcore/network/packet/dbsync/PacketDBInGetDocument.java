/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.dbsync;

import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.packet.api.sync.PacketAPIIO;

import java.util.Map;

/**
 * Created by Tareko on 25.08.2017.
 */
public class PacketDBInGetDocument implements PacketAPIIO {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!packet.getData().contains("name")) {
            Map<String, DatabaseDocument> docs = CloudNet.getInstance()
                                                         .getDatabaseManager()
                                                         .getDatabase(packet.getData().getString("db"))
                                                         .loadDocuments().getDocuments();
            packetSender.sendPacket(getResult(packet, new Document("docs", docs)));
        } else {
            String name = packet.getData().getString("name");
            String db = packet.getData().getString("db");
            DatabaseDocument document = CloudNet.getInstance().getDatabaseManager().getDatabase(db).getDocument(name);
            packetSender.sendPacket(getResult(packet, new Document("result", document)));
        }
    }

    public Packet getResult(Packet packet, Document value) {
        return new Packet(packet.getUniqueId(), PacketRC.DB, value);
    }
}
