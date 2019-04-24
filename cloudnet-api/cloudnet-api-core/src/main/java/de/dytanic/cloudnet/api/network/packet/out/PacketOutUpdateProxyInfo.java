/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 17.08.2017.
 */
public final class PacketOutUpdateProxyInfo extends Packet {

    public PacketOutUpdateProxyInfo(ProxyInfo proxyInfo) {
        super(PacketRC.SERVER_HANDLE + 2, new Document("proxyInfo", proxyInfo));
    }
}