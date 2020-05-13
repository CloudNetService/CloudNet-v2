package eu.cloudnetservice.v2.master.network.packet.out;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 23.07.2017.
 */
public class PacketOutUpdateProxyInfo extends Packet {

    public PacketOutUpdateProxyInfo(ProxyInfo proxyInfo) {
        super(PacketRC.SERVER_HANDLE + 6, new Document("proxyInfo", proxyInfo));
    }
}
