package eu.cloudnetservice.v2.api.network.packet.api.sync;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.utility.document.Document;

import java.util.UUID;

/**
 * Created by Tareko on 17.08.2017.
 */
public class PacketAPIOutGetOfflinePlayer extends Packet {

    public PacketAPIOutGetOfflinePlayer(UUID uniqueId) {
        super(PacketRC.API + 5, new Document("uniqueId", uniqueId));
    }

    public PacketAPIOutGetOfflinePlayer(String name) {
        super(PacketRC.API + 5, new Document("name", name));
    }
}
