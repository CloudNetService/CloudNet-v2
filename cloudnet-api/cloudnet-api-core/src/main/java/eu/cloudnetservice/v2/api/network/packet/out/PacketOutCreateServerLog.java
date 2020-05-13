package eu.cloudnetservice.v2.api.network.packet.out;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 04.10.2017.
 */
public class PacketOutCreateServerLog extends Packet {

    public PacketOutCreateServerLog(String randomString, String serverId) {
        super(PacketRC.CN_INTERNAL_CHANNELS + 1, new Document("random", randomString).append("serverId", serverId));
    }
}
