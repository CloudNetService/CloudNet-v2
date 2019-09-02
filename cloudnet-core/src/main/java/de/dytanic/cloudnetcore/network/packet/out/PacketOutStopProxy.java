/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;

public final class PacketOutStopProxy extends Packet {

    public PacketOutStopProxy(ProxyInfo proxyInfo) {
        super(PacketRC.CN_CORE + 2, new Document("proxyInfo", proxyInfo));
    }
}
