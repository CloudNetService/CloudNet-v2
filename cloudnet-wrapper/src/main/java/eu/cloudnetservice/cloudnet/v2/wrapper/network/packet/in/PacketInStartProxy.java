package eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.in;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyProcessMeta;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;

public final class PacketInStartProxy implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        ProxyProcessMeta proxyProcessMeta = packet.getData().getObject("proxyProcess", ProxyProcessMeta.TYPE);

        System.out.println("Proxy process is now in queue [" + proxyProcessMeta.getServiceId() + ']');
        CloudNetWrapper.getInstance().getServerProcessQueue().putProcess(proxyProcessMeta);
    }
}
