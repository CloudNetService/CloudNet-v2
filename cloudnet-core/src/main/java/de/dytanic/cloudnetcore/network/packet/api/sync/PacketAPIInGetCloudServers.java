/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.CloudServer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tareko on 25.10.2017.
 */
public class PacketAPIInGetCloudServers extends PacketAPIIO {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        List<ServerInfo> serverInfos = CloudNet.getInstance().getCloudGameServers().values()
                                               .stream()
                                               .map(CloudServer::getServerInfo)
                                               .collect(Collectors.toList());
        packetSender.sendPacket(getResult(new Document("serverInfos", serverInfos)));
    }

    @Override
    protected Packet getResult(Document value) {
        return new Packet(packetUniqueId, PacketRC.API, value);
    }
}
