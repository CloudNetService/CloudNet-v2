/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;

public class PacketOutRemoveProxy extends Packet {

    public PacketOutRemoveProxy(ProxyInfo proxyInfo) {
        super(PacketRC.CN_WRAPPER + 4, new Document("proxyInfo", proxyInfo));
    }
}
