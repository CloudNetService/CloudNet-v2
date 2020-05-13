package eu.cloudnetservice.v2.api.database.packet.out;

import eu.cloudnetservice.v2.lib.database.DatabaseDocument;
import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 24.08.2017.
 */
public class PacketDBOutInsertDocument extends Packet {

    public PacketDBOutInsertDocument(String db, DatabaseDocument... documents) {
        super(PacketRC.DB + 2, new Document("insert", documents).append("db", db));
    }
}
