/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 28.08.2017.
 */
public class PacketInOnlineServer extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        /*
        ServerInfo serverInfo = data.getObject("serverInfo", new TypeToken<ServerInfo>(){}.getType());
        GameServer gameServer = CloudNetWrapper.getInstance().getServers().get(serverInfo.getServiceId().getServerId());
        if(gameServer != null)
        CloudNetWrapper.getInstance().getServerProcessQueue().getStartups().remove(gameServer);
        */
    }
}
