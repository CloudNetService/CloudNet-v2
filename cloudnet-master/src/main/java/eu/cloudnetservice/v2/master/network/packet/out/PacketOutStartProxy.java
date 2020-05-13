package eu.cloudnetservice.v2.master.network.packet.out;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.server.ProxyProcessMeta;
import eu.cloudnetservice.v2.lib.utility.document.Document;

public class PacketOutStartProxy extends Packet {

    public PacketOutStartProxy(ProxyProcessMeta proxyProcessMeta) {
        super(PacketRC.CN_CORE + 1, new Document("proxyProcess", proxyProcessMeta));
    }

}
