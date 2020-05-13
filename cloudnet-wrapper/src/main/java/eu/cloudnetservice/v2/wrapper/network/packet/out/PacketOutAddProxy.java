package eu.cloudnetservice.v2.wrapper.network.packet.out;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.server.ProxyProcessMeta;
import eu.cloudnetservice.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.v2.lib.utility.document.Document;

public class PacketOutAddProxy extends Packet {

    public PacketOutAddProxy(ProxyInfo proxyInfo, ProxyProcessMeta proxyProcessMeta) {
        super(PacketRC.CN_WRAPPER + 1, new Document("proxyInfo", proxyInfo).append("proxyProcess", proxyProcessMeta));
    }

}
