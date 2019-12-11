/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;

public final class PacketInAddProxy implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }
        Wrapper wrapper = (Wrapper) packetSender;
        ProxyInfo nullServerInfo = packet.getData().getObject("proxyInfo", ProxyInfo.TYPE);
        ProxyProcessMeta proxyProcessMeta = packet.getData().getObject("proxyProcess", ProxyProcessMeta.TYPE);
        ProxyServer minecraftServer = new ProxyServer(proxyProcessMeta, wrapper, nullServerInfo);
        wrapper.getProxys().put(proxyProcessMeta.getServiceId().getServerId(), minecraftServer);
        wrapper.getWaitingServices().remove(minecraftServer.getServerId());

        CloudNet.getInstance().getNetworkManager().handleProxyAdd(minecraftServer);
    }
}
