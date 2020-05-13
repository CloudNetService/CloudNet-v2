package eu.cloudnetservice.v2.api.database.packet.out;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 24.08.2017.
 */
public class PacketDBOutSelectDatabase extends Packet {

    public PacketDBOutSelectDatabase(String name) {
        super(PacketRC.DB + 6, new Document("name", name));
    }
}
