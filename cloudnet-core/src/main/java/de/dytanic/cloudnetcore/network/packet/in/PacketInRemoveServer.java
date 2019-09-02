/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;

/**
 * Created by Tareko on 20.07.2017.
 */
public class PacketInRemoveServer extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }

        Wrapper cn = (Wrapper) packetSender;
        ServerInfo serverInfo = data.getObject("serverInfo", new TypeToken<ServerInfo>() {}.getType());

        if (cn.getServers().containsKey(serverInfo.getServiceId().getServerId())) {
            MinecraftServer minecraftServer = cn.getServers().get(serverInfo.getServiceId().getServerId());
            if (minecraftServer.getChannel() != null) {
                minecraftServer.getChannel().close();
            }

            cn.getServers().remove(serverInfo.getServiceId().getServerId());
            CloudNet.getInstance().getNetworkManager().handleServerRemove(minecraftServer);
            CloudNet.getInstance().getScreenProvider().handleDisableScreen(serverInfo.getServiceId());
        }
    }
}
