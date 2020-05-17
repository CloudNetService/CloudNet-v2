package eu.cloudnetservice.cloudnet.v2.master.network.packet.in;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;

/**
 * Created by Tareko on 23.07.2017.
 */
public class PacketInUpdateProxyInfo implements PacketInHandler {


    public void handleInput(Packet packet, PacketSender packetSender) {
        if (packetSender instanceof ProxyServer) {
            ProxyServer proxyServer = (ProxyServer) packetSender;
            proxyServer.setLastProxyInfo(proxyServer.getProxyInfo());
            proxyServer.setProxyInfo(packet.getData().getObject("proxyInfo", ProxyInfo.TYPE));
            CloudNet.getInstance().getNetworkManager().handleProxyInfoUpdate(
                proxyServer, packet.getData().getObject("proxyInfo", ProxyInfo.TYPE));
        }
    }
}
