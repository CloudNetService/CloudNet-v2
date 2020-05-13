package eu.cloudnetservice.v2.master.network.packet.out;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.v2.lib.utility.document.Document;

public class PacketOutOnlineServer extends Packet {

    public PacketOutOnlineServer(ServerInfo serverInfo) {
        super(PacketRC.CN_CORE + 11, new Document("serverInfo", serverInfo));
    }
}
