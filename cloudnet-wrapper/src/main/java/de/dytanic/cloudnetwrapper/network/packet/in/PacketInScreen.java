/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;

public final class PacketInScreen extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender)
    {
        /*
        if (data.getObject("type", DefaultType.class) != DefaultType.BUNGEE_CORD)
        {
            ServerInfo server = data.getObject("serverInfo", new TypeToken<ServerInfo>() {
            }.getType());
            if (CloudNetWrapper.getInstance().getServers().containsKey(server.getServiceId().getServerId()))
            {
                GameServer gameServer = CloudNetWrapper.getInstance().getServers().get(server.getServiceId().getServerId());
                if (data.getBoolean("enable"))
                    CloudNetWrapper.getInstance().getScreenProvider().putScreenRequest(gameServer);
                else
                    CloudNetWrapper.getInstance().getScreenProvider().cancel(gameServer);
            }
        } else
        {
            ProxyInfo server = data.getObject("proxyInfo", new TypeToken<ProxyInfo>() {
            }.getType());
            if (CloudNetWrapper.getInstance().getProxys().containsKey(server.getServiceId().getServerId()))
            {
                BungeeCord gameServer = CloudNetWrapper.getInstance().getProxys().get(server.getServiceId().getServerId());
                if (data.getBoolean("enable"))
                    CloudNetWrapper.getInstance().getScreenProvider().putScreenRequest(gameServer);
                else if (CloudNetWrapper.getInstance().getScreenProvider().contains(gameServer))
                    CloudNetWrapper.getInstance().getScreenProvider().cancel(gameServer);
            }
        }
        */
    }
}