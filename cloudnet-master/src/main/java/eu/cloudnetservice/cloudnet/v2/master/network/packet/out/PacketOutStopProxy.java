package eu.cloudnetservice.cloudnet.v2.master.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

public final class PacketOutStopProxy extends Packet {

    public PacketOutStopProxy(ProxyInfo proxyInfo) {
        super(PacketRC.CN_CORE + 2, new Document("proxyInfo", proxyInfo));
    }
}
