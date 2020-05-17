package eu.cloudnetservice.cloudnet.v2.lib.network.auth.packetio;

import eu.cloudnetservice.cloudnet.v2.lib.network.auth.Auth;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 22.07.2017.
 */
public class PacketOutAuth extends Packet {

    public PacketOutAuth(Auth auth) {
        super(PacketRC.INTERNAL - 1, new Document().append("auth", auth));
    }
}
