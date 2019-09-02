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
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketAPIInGetServers extends PacketAPIIO {

    @Override
    public void handleInput(Document data, PacketSender packetSender)
    {
        if (packetUniqueId == null) return;
        if (data.contains("group"))
        {
            Collection<ServerInfo> proxyInfos = CloudNet.getInstance().getServers(data.getString("group")).stream().map(MinecraftServer::getServerInfo).collect(Collectors.toList());;

            packetSender.sendPacket(getResult(new Document("serverInfos", proxyInfos)));
        } else
        {
            Collection<ServerInfo> proxyInfos = CloudNet.getInstance().getServers().values().stream().map(MinecraftServer::getServerInfo).collect(Collectors.toList());

            packetSender.sendPacket(getResult(new Document("serverInfos", proxyInfos)));
        }
    }

    @Override
    protected Packet getResult(Document value)
    {
        return new Packet(packetUniqueId, PacketRC.SERVER_HANDLE, value);
    }
}