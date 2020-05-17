package eu.cloudnetservice.cloudnet.v2.api.network.packet.api.sync;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.util.UUID;

/**
 * Created by Tareko on 20.08.2017.
 */
public class PacketAPIOutNameUUID extends Packet {

    public PacketAPIOutNameUUID(String name) {
        super(PacketRC.API + 7, new Document("name", name));
    }

    public PacketAPIOutNameUUID(UUID uniqueId) {
        super(PacketRC.API + 7, new Document("uniqueId", uniqueId));
    }
}
