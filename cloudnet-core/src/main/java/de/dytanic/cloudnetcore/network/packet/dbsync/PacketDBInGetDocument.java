/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.dbsync;

import de.dytanic.cloudnet.database.DatabaseImpl;
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
public class PacketDBInGetDocument extends PacketAPIIO {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        if (!data.contains("name")) {
            Map<String, Document> docs = ((DatabaseImpl) CloudNet.getInstance()
                                                                 .getDatabaseManager()
                                                                 .getDatabase(data.getString("db"))
                                                                 .loadDocuments()).getDocuments();
            packetSender.sendPacket(getResult(new Document("docs", docs)));
        } else {
            String x = data.getString("name");
            String db = data.getString("db");
            Document document = CloudNet.getInstance().getDatabaseManager().getDatabase(db).getDocument(x);
            packetSender.sendPacket(getResult(new Document("result", document)));
        }
    }

    @Override
    protected Packet getResult(Document value) {
        return new Packet(packetUniqueId, PacketRC.DB, value);
    }
}
