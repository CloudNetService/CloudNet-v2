package de.dytanic.cloudnet.lib.network.auth.packetio;

import de.dytanic.cloudnet.lib.network.auth.AuthLoginResult;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 25.07.2017.
 */
public class PacketOutAuthResult extends Packet {

    public PacketOutAuthResult(AuthLoginResult loginResult) {
        super(PacketRC.INTERNAL - 2, new Document("result", loginResult));
    }
}
