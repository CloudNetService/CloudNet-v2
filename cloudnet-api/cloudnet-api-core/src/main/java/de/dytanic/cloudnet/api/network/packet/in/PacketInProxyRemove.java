/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.in;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.network.packet.PacketInHandlerDefault;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;

/**
 * Created by Tareko on 17.08.2017.
 */
public final class PacketInProxyRemove implements PacketInHandlerDefault {
    public void handleInput(Packet packet, PacketSender packetSender) {
        ProxyInfo proxyInfo = packet.getData().getObject("proxyInfo", ProxyInfo.TYPE);
        if (CloudAPI.getInstance() != null) {
            CloudAPI.getInstance().getNetworkHandlerProvider().iterator(
                obj -> obj.onProxyRemove(proxyInfo));
        }
    }
}
