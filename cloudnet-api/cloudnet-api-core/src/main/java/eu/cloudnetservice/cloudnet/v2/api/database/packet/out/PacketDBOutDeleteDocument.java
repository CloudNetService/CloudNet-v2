package eu.cloudnetservice.cloudnet.v2.api.database.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.database.Database;
import eu.cloudnetservice.cloudnet.v2.lib.database.DatabaseDocument;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 24.08.2017.
 */
public class PacketDBOutDeleteDocument extends Packet {

    public PacketDBOutDeleteDocument(DatabaseDocument document, String db) {
        this(document.getString(Database.UNIQUE_NAME_KEY), db);
    }

    public PacketDBOutDeleteDocument(String name, String db) {
        super(PacketRC.DB + 3, new Document("name", name).append("db", db));
    }
}
