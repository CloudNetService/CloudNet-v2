package eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

public class PacketOutSetReadyWrapper extends Packet {

    public PacketOutSetReadyWrapper(boolean ready) {
        super(PacketRC.CN_WRAPPER + 7, new Document("ready", ready));
    }
}
