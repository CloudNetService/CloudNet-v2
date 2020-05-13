package eu.cloudnetservice.v2.wrapper.network.packet.in;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.v2.lib.server.template.TemplateResource;
import eu.cloudnetservice.v2.wrapper.CloudNetWrapper;
import eu.cloudnetservice.v2.wrapper.server.GameServer;

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
