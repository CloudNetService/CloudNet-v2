/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.CloudServer;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;

/**
 * Created by Tareko on 31.08.2017.
 */
public class PacketAPIInGetServer extends PacketAPIIO {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        MinecraftServer minecraftServer = CloudNet.getInstance().getServer(data.getString("server"));
        if (minecraftServer != null) {
            packetSender.sendPacket(getResult(new Document("serverInfo", minecraftServer.getServerInfo())));
        } else {
            CloudServer cloudServer = CloudNet.getInstance().getCloudGameServer(data.getString("server"));
            if (cloudServer != null) {
                packetSender.sendPacket(getResult(new Document("serverInfo", cloudServer.getServerInfo())));
                return;
            } else {
                packetSender.sendPacket(getResult(new Document()));
            }
        }
    }

    @Override
    protected Packet getResult(Document value) {
        return new Packet(packetUniqueId, PacketRC.API, value);
    }
}
