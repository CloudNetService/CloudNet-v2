package eu.cloudnetservice.v2.wrapper.network.packet.out;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.server.ServerProcessMeta;
import eu.cloudnetservice.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.v2.lib.utility.document.Document;

public class PacketOutAddServer extends Packet {

    public PacketOutAddServer(ServerInfo serverInfo, ServerProcessMeta serverProcessMeta) {
        super(PacketRC.CN_WRAPPER + 2, new Document("serverInfo", serverInfo).append("serverProcess", serverProcessMeta));
    }
}
