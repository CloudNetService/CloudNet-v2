/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 23.07.2017.
 */
public class PacketOutUpdateProxyInfo extends Packet {

    public PacketOutUpdateProxyInfo(ProxyInfo proxyInfo) {
        super(PacketRC.SERVER_HANDLE + 6, new Document("proxyInfo", proxyInfo));
    }
}
