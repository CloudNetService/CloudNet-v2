/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;

public final class PacketInAddProxy extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }
        Wrapper cn = ((Wrapper) packetSender);
        ProxyInfo nullServerInfo = data.getObject("proxyInfo", new TypeToken<ProxyInfo>() {}.getType());
        ProxyProcessMeta proxyProcessMeta = data.getObject("proxyProcess", new TypeToken<ProxyProcessMeta>() {}.getType());
        ProxyServer minecraftServer = new ProxyServer(proxyProcessMeta, cn, nullServerInfo);
        cn.getProxys().put(proxyProcessMeta.getServiceId().getServerId(), minecraftServer);
        cn.getWaitingServices().remove(minecraftServer.getServerId());

        CloudNet.getInstance().getNetworkManager().handleProxyAdd(minecraftServer);
    }
}
