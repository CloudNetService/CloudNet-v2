package eu.cloudnetservice.cloudnet.v2.master.network.packet.api;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInStopProxy implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        String proxyId = packet.getData().getString("serverId");
        ProxyServer proxyServer = CloudNet.getInstance().getProxy(proxyId);
        if (proxyServer != null) {
            proxyServer.getWrapper().stopProxy(proxyServer);
        }
    }
}
