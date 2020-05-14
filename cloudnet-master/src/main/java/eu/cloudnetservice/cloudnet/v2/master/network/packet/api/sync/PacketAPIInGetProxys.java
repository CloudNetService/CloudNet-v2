package eu.cloudnetservice.cloudnet.v2.master.network.packet.api.sync;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketAPIInGetProxys implements PacketAPIIO {

    public void handleInput(Packet packet, PacketSender packetSender) {
        Stream<ProxyServer> proxyServers = CloudNet.getInstance().getProxys().values().stream();

        if (packet.getData().contains("group")) {
            proxyServers = proxyServers.filter(
                proxyServer ->
                    proxyServer.getServiceId().getGroup().equals(packet.getData().getString("group")));
        }
        List<ProxyInfo> proxyInfos = proxyServers
            .map(ProxyServer::getProxyInfo)
            .collect(Collectors.toList());
        packetSender.sendPacket(getResult(packet, new Document("proxyInfos", proxyInfos)));
    }

    public Packet getResult(Packet packet, Document value) {
        return new Packet(packet.getUniqueId(), PacketRC.SERVER_HANDLE, value);
    }
}
