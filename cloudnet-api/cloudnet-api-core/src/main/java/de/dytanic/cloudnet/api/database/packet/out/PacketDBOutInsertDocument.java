package de.dytanic.cloudnet.api.database.packet.out;

import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 24.08.2017.
 */
public class PacketDBOutInsertDocument extends Packet {

    public PacketDBOutInsertDocument(String db, DatabaseDocument... documents) {
        super(PacketRC.DB + 2, new Document("insert", documents).append("db", db));
    }
}
