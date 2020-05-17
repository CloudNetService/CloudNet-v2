package eu.cloudnetservice.cloudnet.v2.api.network.packet.api;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyGroup;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketOutUpdateProxyGroup extends Packet {

    public PacketOutUpdateProxyGroup(ProxyGroup proxyGroup) {
        super(PacketRC.CN_CORE + 3, new Document("group", proxyGroup));
    }
}
