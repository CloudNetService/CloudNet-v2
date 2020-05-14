package eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.in;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerProcessMeta;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;

public final class PacketInStartServer implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        ServerProcessMeta serverProcessMeta = packet.getData().getObject("serverProcess", ServerProcessMeta.TYPE);

        System.out.println("Server process is now in queue [" + serverProcessMeta.getServiceId() + ']');
        CloudNetWrapper.getInstance().getServerProcessQueue().putProcess(serverProcessMeta);
    }
}
