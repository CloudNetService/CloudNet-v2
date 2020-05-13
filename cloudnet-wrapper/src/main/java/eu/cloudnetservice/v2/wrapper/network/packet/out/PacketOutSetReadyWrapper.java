package eu.cloudnetservice.v2.wrapper.network.packet.out;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.utility.document.Document;

public class PacketOutSetReadyWrapper extends Packet {

    public PacketOutSetReadyWrapper(boolean ready) {
        super(PacketRC.CN_WRAPPER + 7, new Document("ready", ready));
    }
}
