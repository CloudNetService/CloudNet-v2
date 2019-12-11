/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.CloudServer;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;

/**
 * Created by Tareko on 23.07.2017.
 */
public class PacketInUpdateServerInfo implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (packetSender instanceof MinecraftServer) {
            MinecraftServer minecraftServer = (MinecraftServer) packetSender;
            minecraftServer.setLastServerInfo(minecraftServer.getServerInfo());
            minecraftServer.setServerInfo(packet.getData().getObject("serverInfo", ServerInfo.TYPE));
            CloudNet.getInstance().getNetworkManager().handleServerInfoUpdate(
                minecraftServer, packet.getData().getObject("serverInfo", ServerInfo.TYPE));
        }
        if (packetSender instanceof CloudServer) {
            CloudServer cloudServer = (CloudServer) packetSender;
            cloudServer.setLastServerInfo(cloudServer.getServerInfo());
            cloudServer.setServerInfo(packet.getData().getObject("serverInfo", ServerInfo.TYPE));
            CloudNet.getInstance().getNetworkManager().handleServerInfoUpdate(
                cloudServer, packet.getData().getObject("serverInfo", ServerInfo.TYPE));
        }
    }
}
