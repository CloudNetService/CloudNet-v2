/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

public class PacketInExecuteServerCommand extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        if (data.getObject("type", DefaultType.class).equals(DefaultType.BUKKIT)) {
            ServerInfo serverInfo = data.getObject("serverInfo", new TypeToken<ServerInfo>() {}.getType());
            if (CloudNetWrapper.getInstance().getServers().containsKey(serverInfo.getServiceId().getServerId())) {
                CloudNetWrapper.getInstance().getServers().get(serverInfo.getServiceId().getServerId()).executeCommand(data.getString(
                    "commandLine"));
            }
        } else {
            ProxyInfo serverInfo = data.getObject("proxyInfo", new TypeToken<ProxyInfo>() {}.getType());
            if (CloudNetWrapper.getInstance().getProxys().containsKey(serverInfo.getServiceId().getServerId())) {
                CloudNetWrapper.getInstance().getProxys().get(serverInfo.getServiceId().getServerId()).executeCommand(data.getString(
                    "commandLine"));
            }
        }
    }
}
