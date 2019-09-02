/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.api;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketOutUpdateProxyGroup extends Packet {

    public PacketOutUpdateProxyGroup(ProxyGroup proxyGroup) {
        super(PacketRC.CN_CORE + 3, new Document("group", proxyGroup));
    }
}
