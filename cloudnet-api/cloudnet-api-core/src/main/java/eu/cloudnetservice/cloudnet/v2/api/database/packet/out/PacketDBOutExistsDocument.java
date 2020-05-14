package eu.cloudnetservice.cloudnet.v2.api.database.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 24.08.2017.
 */
public class PacketDBOutExistsDocument extends Packet {

    public PacketDBOutExistsDocument(String name, String db) {
        super(PacketRC.DB + 4, new Document("name", name).append("db", db));
    }
}
