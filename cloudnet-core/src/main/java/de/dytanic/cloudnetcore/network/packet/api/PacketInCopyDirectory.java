package de.dytanic.cloudnetcore.network.packet.api;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;

public class PacketInCopyDirectory extends PacketInHandler {

    public PacketInCopyDirectory() {
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        if (!data.contains("serverInfo") || !data.contains("directory")) {
            return;
        }

        ServerInfo info = data.getObject("serverInfo", ServerInfo.TYPE);

        Wrapper wrapper = CloudNet.getInstance().getWrappers().get(info.getServiceId().getWrapperId());

        if (wrapper != null && wrapper.getChannel() != null) {
            wrapper.sendPacket(new Packet(PacketRC.CN_CORE + 14, data));
        }
    }
}
