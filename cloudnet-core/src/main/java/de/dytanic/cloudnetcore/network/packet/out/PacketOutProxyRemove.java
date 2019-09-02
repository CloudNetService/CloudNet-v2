/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 17.08.2017.
 */
public final class PacketOutProxyRemove extends Packet {

    public PacketOutProxyRemove(ProxyInfo proxyInfo) {
        super(PacketRC.SERVER_HANDLE + 7, new Document("proxyInfo", proxyInfo));
    }
}
