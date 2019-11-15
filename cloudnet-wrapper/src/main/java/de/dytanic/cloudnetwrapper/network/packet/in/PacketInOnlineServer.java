/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;

/**
 * Created by Tareko on 28.08.2017.
 */
public class PacketInOnlineServer implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        /*
        ServerInfo serverInfo = data.getObject("serverInfo", new TypeToken<ServerInfo>(){}.getType());
        GameServer gameServer = CloudNetWrapper.getInstance().getServers().get(serverInfo.getServiceId().getServerId());
        if(gameServer != null)
        CloudNetWrapper.getInstance().getServerProcessQueue().getStartups().remove(gameServer);
        */
    }
}
