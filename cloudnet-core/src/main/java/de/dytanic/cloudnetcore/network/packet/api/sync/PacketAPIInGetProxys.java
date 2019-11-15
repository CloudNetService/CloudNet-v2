/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.ProxyServer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketAPIInGetProxys implements PacketAPIIO {

    public void handleInput(Packet packet, PacketSender packetSender) {
        Stream<ProxyServer> proxyServers = CloudNet.getInstance().getProxys().values().stream();

        if (packet.getData().contains("group")) {
            proxyServers = proxyServers.filter(
                proxyServer ->
                    proxyServer.getServiceId().getGroup().equals(packet.getData().getString("group")));
        }
        List<ProxyInfo> proxyInfos = proxyServers
            .map(ProxyServer::getProxyInfo)
            .collect(Collectors.toList());
        packetSender.sendPacket(getResult(packet, new Document("proxyInfos", proxyInfos)));
    }

    public Packet getResult(Packet packet, Document value) {
        return new Packet(packet.getUniqueId(), PacketRC.SERVER_HANDLE, value);
    }
}
