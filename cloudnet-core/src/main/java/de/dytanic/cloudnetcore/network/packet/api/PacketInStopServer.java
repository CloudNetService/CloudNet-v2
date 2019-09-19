/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api;

import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.CloudServer;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInStopServer extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        String serverId = data.getString("serverId");
        MinecraftServer minecraftServer = CloudNet.getInstance().getServer(serverId);
        if (minecraftServer != null) {
            minecraftServer.getWrapper().stopServer(minecraftServer);
            return;
        }
        CloudServer cloudServer = CloudNet.getInstance().getCloudGameServer(serverId);
        if (cloudServer != null) {
            cloudServer.getWrapper().stopServer(cloudServer);
        }
    }
}
