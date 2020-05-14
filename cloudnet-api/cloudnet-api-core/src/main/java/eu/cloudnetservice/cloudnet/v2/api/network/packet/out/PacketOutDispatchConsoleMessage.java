package eu.cloudnetservice.cloudnet.v2.api.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketOutDispatchConsoleMessage extends Packet {

    public PacketOutDispatchConsoleMessage(String message) {
        super(PacketRC.CN_WRAPPER + 3, new Document("output", message));
    }
}
