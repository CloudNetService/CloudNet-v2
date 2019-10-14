/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.CloudServer;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInServerDispatchCommand extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        DefaultType defaultType = data.getObject("defaultType", new TypeToken<DefaultType>() {}.getType());
        String serverId = data.getString("serverId");
        String commandLine = data.getString("commandLine");

        if (defaultType == DefaultType.BUKKIT) {
            MinecraftServer minecraftServer = CloudNet.getInstance().getServer(serverId);
            if (minecraftServer != null) {
                minecraftServer.getWrapper().writeServerCommand(commandLine, minecraftServer.getServerInfo());
            }
            CloudServer cloudServer = CloudNet.getInstance().getCloudGameServer(serverId);
            if (cloudServer != null) {
                cloudServer.getWrapper().writeServerCommand(commandLine, minecraftServer.getServerInfo());
            }
        } else {
            ProxyServer proxyServer = CloudNet.getInstance().getProxy(serverId);
            if (proxyServer != null) {
                proxyServer.getWrapper().writeProxyCommand(commandLine, proxyServer.getProxyInfo());
            }
        }
    }
}
