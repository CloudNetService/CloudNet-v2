package eu.cloudnetservice.cloudnet.v2.master.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

public class PacketOutInstallUpdate extends Packet {

    public PacketOutInstallUpdate(String url) {
        super(PacketRC.CN_CORE + 8, new Document("url", url));
    }
}
