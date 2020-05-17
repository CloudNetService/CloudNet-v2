package eu.cloudnetservice.cloudnet.v2.api.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 17.08.2017.
 */
public final class PacketOutUpdateProxyInfo extends Packet {

    public PacketOutUpdateProxyInfo(ProxyInfo proxyInfo) {
        super(PacketRC.SERVER_HANDLE + 2, new Document("proxyInfo", proxyInfo));
    }
}
