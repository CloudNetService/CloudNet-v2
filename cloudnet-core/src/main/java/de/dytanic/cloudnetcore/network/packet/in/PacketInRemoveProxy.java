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
import de.dytanic.cloudnetcore.network.components.Wrapper;

public final class PacketInRemoveProxy extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }

        Wrapper cn = (Wrapper) packetSender;
        ProxyInfo proxyInfo = data.getObject("proxyInfo", new TypeToken<ProxyInfo>() {}.getType());

        if (cn.getProxys().containsKey(proxyInfo.getServiceId().getServerId())) {
            ProxyServer minecraftServer = cn.getProxys().get(proxyInfo.getServiceId().getServerId());
            if (minecraftServer.getChannel() != null) {
                minecraftServer.getChannel().close();
            }

            cn.getProxys().remove(proxyInfo.getServiceId().getServerId());
            CloudNet.getInstance().getNetworkManager().handleProxyRemove(minecraftServer);
            CloudNet.getInstance().getScreenProvider().handleDisableScreen(proxyInfo.getServiceId());
        }
    }
}
