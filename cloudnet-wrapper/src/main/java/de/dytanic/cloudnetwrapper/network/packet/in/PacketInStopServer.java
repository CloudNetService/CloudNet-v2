package de.dytanic.cloudnetwrapper.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

public class PacketInStopServer implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        ServerInfo serverInfo = packet.getData().getObject("serverInfo", ServerInfo.TYPE);
        if (CloudNetWrapper.getInstance().getServers().containsKey(serverInfo.getServiceId().getServerId())) {
            CloudNetWrapper.getInstance().getServers().get(serverInfo.getServiceId().getServerId()).shutdown();
        } else if (CloudNetWrapper.getInstance().getCloudServers().containsKey(serverInfo.getServiceId().getServerId())) {
            CloudNetWrapper.getInstance().getCloudServers().get(serverInfo.getServiceId().getServerId()).shutdown();
        }
    }
}
