/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;

/**
 * Created by Tareko on 20.07.2017.
 */
public class PacketInRemoveServer implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }

        Wrapper wrapper = (Wrapper) packetSender;
        ServerInfo serverInfo = packet.getData().getObject("serverInfo", ServerInfo.TYPE);

        if (wrapper.getServers().containsKey(serverInfo.getServiceId().getServerId())) {
            MinecraftServer minecraftServer = wrapper.getServers().get(serverInfo.getServiceId().getServerId());
            if (minecraftServer.getChannel() != null) {
                minecraftServer.getChannel().close();
            }

            wrapper.getServers().remove(serverInfo.getServiceId().getServerId());
            CloudNet.getInstance().getNetworkManager().handleServerRemove(minecraftServer);
            CloudNet.getInstance().getScreenProvider().handleDisableScreen(serverInfo.getServiceId());
        }
    }
}
