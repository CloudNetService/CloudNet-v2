package eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

public class PacketOutRemoveServer extends Packet {

    public PacketOutRemoveServer(ServerInfo serverInfo) {
        super(PacketRC.CN_WRAPPER + 5, new Document("serverInfo", serverInfo));
    }
}
