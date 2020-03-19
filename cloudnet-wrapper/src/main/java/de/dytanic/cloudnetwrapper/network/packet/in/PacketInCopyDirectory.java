package de.dytanic.cloudnetwrapper.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.server.GameServer;

public final class PacketInCopyDirectory implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!packet.getData().contains("directory") || !packet.getData().contains("serverInfo")) {
            return;
        }

        ServerInfo serverInfo = packet.getData().getObject("serverInfo", ServerInfo.TYPE);
        GameServer gameServer = CloudNetWrapper.getInstance().getServers().get(serverInfo.getServiceId().getServerId());

        if (gameServer == null) {
            return;
        }

        if (gameServer.getServerProcess().getMeta().getTemplate().getBackend().equals(TemplateResource.LOCAL)) {
            gameServer.copyDirectory(packet.getData().getString("directory"));
        }

    }
}
