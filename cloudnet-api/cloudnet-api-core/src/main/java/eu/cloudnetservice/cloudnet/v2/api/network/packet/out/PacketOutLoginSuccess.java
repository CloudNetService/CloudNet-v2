package eu.cloudnetservice.cloudnet.v2.api.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.util.UUID;

/**
 * Created by Tareko on 05.10.2017.
 */
public class PacketOutLoginSuccess extends Packet {

    public PacketOutLoginSuccess(UUID uniqueId) {
        super(PacketRC.PLAYER_HANDLE + 6, new Document("uniqueId", uniqueId));
    }
}
