package eu.cloudnetservice.v2.master.network.packet.out;

import eu.cloudnetservice.v2.lib.DefaultType;
import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.v2.lib.utility.document.Document;

public class PacketOutScreen extends Packet {

    public PacketOutScreen(ServerInfo serverInfo, DefaultType type, boolean enable) {
        super(PacketRC.CN_CORE + 6, new Document("serverInfo", serverInfo).append("type", type).append("enable", enable));
    }

    public PacketOutScreen(ProxyInfo serverInfo, DefaultType type, boolean enable) {
        super(PacketRC.CN_CORE + 6, new Document("proxyInfo", serverInfo).append("type", type).append("enable", enable));
    }
}
