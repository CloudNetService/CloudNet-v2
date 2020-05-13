package eu.cloudnetservice.v2.master.network.packet.api;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.components.ProxyServer;

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
