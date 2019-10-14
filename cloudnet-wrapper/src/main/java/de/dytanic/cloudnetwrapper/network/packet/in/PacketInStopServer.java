/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

public class PacketInStopServer extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        ServerInfo serverInfo = data.getObject("serverInfo", new TypeToken<ServerInfo>() {}.getType());
        if (CloudNetWrapper.getInstance().getServers().containsKey(serverInfo.getServiceId().getServerId())) {
            CloudNetWrapper.getInstance().getServers().get(serverInfo.getServiceId().getServerId()).shutdown();
            return;
        }

        if (CloudNetWrapper.getInstance().getCloudServers().containsKey(serverInfo.getServiceId().getServerId())) {
            CloudNetWrapper.getInstance().getCloudServers().get(serverInfo.getServiceId().getServerId()).shutdown();
        }
    }
}
