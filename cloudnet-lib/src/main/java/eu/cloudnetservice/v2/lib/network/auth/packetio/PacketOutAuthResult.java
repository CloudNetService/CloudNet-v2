package eu.cloudnetservice.v2.lib.network.auth.packetio;

import eu.cloudnetservice.v2.lib.network.auth.AuthLoginResult;
import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 25.07.2017.
 */
public class PacketOutAuthResult extends Packet {

    public PacketOutAuthResult(AuthLoginResult loginResult) {
        super(PacketRC.INTERNAL - 2, new Document("result", loginResult));
    }
}
