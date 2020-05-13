package eu.cloudnetservice.v2.master.network.packet.api;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.components.Wrapper;

public class PacketInCopyDirectory implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!packet.getData().contains("serverInfo") || !packet.getData().contains("directory")) {
            return;
        }

        ServerInfo info = packet.getData().getObject("serverInfo", ServerInfo.TYPE);

        Wrapper wrapper = CloudNet.getInstance().getWrappers().get(info.getServiceId().getWrapperId());

        if (wrapper != null && wrapper.getChannel() != null) {
            wrapper.sendPacket(new Packet(PacketRC.CN_CORE + 14, packet.getData()));
        }
    }
}
