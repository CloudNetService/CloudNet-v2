package de.dytanic.cloudnet.lib.network.auth.packetio;

import de.dytanic.cloudnet.lib.network.auth.Auth;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 22.07.2017.
 */
public class PacketOutAuth extends Packet {

    public PacketOutAuth(Auth auth) {
        super(PacketRC.INTERNAL - 1, new Document().append("auth", auth));
    }
}
