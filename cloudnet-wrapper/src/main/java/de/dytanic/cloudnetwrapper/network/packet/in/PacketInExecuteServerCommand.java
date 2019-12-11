/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

public class PacketInExecuteServerCommand implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (packet.getData().getObject("type", DefaultType.class).equals(DefaultType.BUKKIT)) {
            ServerInfo serverInfo = packet.getData().getObject("serverInfo", ServerInfo.TYPE);
            if (CloudNetWrapper.getInstance().getServers().containsKey(serverInfo.getServiceId().getServerId())) {
                CloudNetWrapper.getInstance().getServers().get(
                    serverInfo.getServiceId().getServerId()).executeCommand(
                    packet.getData().getString("commandLine"));
            }
        } else {
            ProxyInfo serverInfo = packet.getData().getObject("proxyInfo", ProxyInfo.TYPE);
            if (CloudNetWrapper.getInstance().getProxys().containsKey(serverInfo.getServiceId().getServerId())) {
                CloudNetWrapper.getInstance().getProxys().get(
                    serverInfo.getServiceId().getServerId()).executeCommand(
                    packet.getData().getString("commandLine"));
            }
        }
    }
}
