/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.ProxyServer;

import java.lang.reflect.Type;

/**
 * Created by Tareko on 23.07.2017.
 */
public class PacketInUpdateProxyInfo extends PacketInHandler {

    private static final Type type = new TypeToken<ProxyInfo>() {}.getType();

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        if (packetSender instanceof ProxyServer) {
            ((ProxyServer) packetSender).setLastProxyInfo(((ProxyServer) packetSender).getProxyInfo());
            ((ProxyServer) packetSender).setProxyInfo(data.getObject("proxyInfo", type));
            CloudNet.getInstance().getNetworkManager().handleProxyInfoUpdate(((ProxyServer) packetSender),
                                                                             data.getObject("proxyInfo", type));
        }
    }
}
