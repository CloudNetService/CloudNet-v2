/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;

public class PacketOutAddProxy extends Packet {

    public PacketOutAddProxy(ProxyInfo proxyInfo, ProxyProcessMeta proxyProcessMeta) {
        super(PacketRC.CN_WRAPPER + 1, new Document("proxyInfo", proxyInfo).append("proxyProcess", proxyProcessMeta));
    }

}
