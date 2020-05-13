package eu.cloudnetservice.v2.wrapper.network.packet.out;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.v2.lib.utility.document.Document;

public class PacketOutRemoveProxy extends Packet {

    public PacketOutRemoveProxy(ProxyInfo proxyInfo) {
        super(PacketRC.CN_WRAPPER + 4, new Document("proxyInfo", proxyInfo));
    }
}
