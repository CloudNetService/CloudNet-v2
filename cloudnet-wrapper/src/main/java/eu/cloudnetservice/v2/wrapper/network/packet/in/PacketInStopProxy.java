package eu.cloudnetservice.v2.wrapper.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import eu.cloudnetservice.v2.wrapper.CloudNetWrapper;

public class PacketInStopProxy implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        ProxyInfo serverInfo = packet.getData().getObject("proxyInfo", ProxyInfo.TYPE);
        if (CloudNetWrapper.getInstance().getProxies().containsKey(serverInfo.getServiceId().getServerId())) {
            CloudNetWrapper.getInstance().getProxies().get(serverInfo.getServiceId().getServerId()).shutdown();
        }
    }
}
