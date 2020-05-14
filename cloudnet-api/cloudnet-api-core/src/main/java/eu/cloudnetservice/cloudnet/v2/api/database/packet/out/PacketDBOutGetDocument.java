package eu.cloudnetservice.cloudnet.v2.api.database.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 24.08.2017.
 */
public class PacketDBOutGetDocument extends Packet {

    public PacketDBOutGetDocument(String name, String db) {
        super(PacketRC.DB + 1, new Document("name", name).append("db", db));
    }

    public PacketDBOutGetDocument(String db) {
        super(PacketRC.DB + 1, new Document("all", true).append("db", db));
    }
}
