package eu.cloudnetservice.v2.api.database.packet.out;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 24.08.2017.
 */
public class PacketDBOutGetSize extends Packet {

    public PacketDBOutGetSize(String name) {
        super(PacketRC.DB + 5, new Document("name", name));
    }
}
