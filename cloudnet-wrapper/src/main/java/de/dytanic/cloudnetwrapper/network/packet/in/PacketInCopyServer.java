/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.server.GameServer;

/**
 * Created by Tareko on 28.08.2017.
 */
public class PacketInCopyServer implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        ServerInfo serverInfo = packet.getData().getObject("serverInfo", ServerInfo.TYPE);

        GameServer gameServer = CloudNetWrapper.getInstance().getServers().get(serverInfo.getServiceId().getServerId());
        if (gameServer != null) {
            if (!packet.getData().contains("template")) {
                CloudNetWrapper.getInstance().getScheduler().runTaskAsync(gameServer::copy);
            } else {
                CloudNetWrapper.getInstance().getScheduler().runTaskAsync(
                    () -> gameServer.copy(packet.getData().getObject("template", Template.TYPE)));
            }
        }
    }
}
