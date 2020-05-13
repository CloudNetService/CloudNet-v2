package de.dytanic.cloudnetcore.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketAPIInGetServers implements PacketAPIIO {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (packet.getUniqueId() == null) {
            return;
        }
        Stream<MinecraftServer> servers = CloudNet.getInstance().getServers().values().stream();
        if (packet.getData().contains("group")) {
            servers = servers.filter(server -> server.getServiceId().getGroup().equals(packet.getData().getString("group")));
        }
        List<ServerInfo> serverInfos = servers
            .map(MinecraftServer::getServerInfo)
            .collect(Collectors.toList());
        packetSender.sendPacket(getResult(packet, new Document("serverInfos", serverInfos)));
    }

    public Packet getResult(Packet packet, Document value) {
        return new Packet(packet.getUniqueId(), PacketRC.SERVER_HANDLE, value);
    }
}
