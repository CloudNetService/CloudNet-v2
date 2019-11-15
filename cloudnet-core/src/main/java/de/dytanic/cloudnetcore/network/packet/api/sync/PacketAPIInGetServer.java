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
public class PacketAPIInGetServer implements PacketAPIIO {

    public void handleInput(Packet packet, PacketSender packetSender) {
        MinecraftServer minecraftServer = CloudNet.getInstance().getServer(packet.getData().getString("server"));
        if (minecraftServer != null) {
            packetSender.sendPacket(getResult(packet, new Document("serverInfo", minecraftServer.getServerInfo())));
        } else {
            CloudServer cloudServer = CloudNet.getInstance().getCloudGameServer(packet.getData().getString("server"));
            if (cloudServer != null) {
                packetSender.sendPacket(getResult(packet, new Document("serverInfo", cloudServer.getServerInfo())));
            } else {
                packetSender.sendPacket(getResult(packet, new Document()));
            }
        }
    }

    public Packet getResult(Packet packet, Document value) {
        return new Packet(packet.getUniqueId(), PacketRC.API, value);
    }
}
