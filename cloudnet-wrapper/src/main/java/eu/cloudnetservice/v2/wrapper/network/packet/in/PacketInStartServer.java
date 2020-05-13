package eu.cloudnetservice.v2.wrapper.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ServerProcessMeta;
import eu.cloudnetservice.v2.wrapper.CloudNetWrapper;

public final class PacketInStartServer implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        ServerProcessMeta serverProcessMeta = packet.getData().getObject("serverProcess", ServerProcessMeta.TYPE);

        System.out.println("Server process is now in queue [" + serverProcessMeta.getServiceId() + ']');
        CloudNetWrapper.getInstance().getServerProcessQueue().putProcess(serverProcessMeta);
    }
}
