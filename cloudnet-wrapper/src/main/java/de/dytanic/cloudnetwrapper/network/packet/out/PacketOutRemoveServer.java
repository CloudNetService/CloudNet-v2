/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;

public class PacketOutRemoveServer extends Packet {

    public PacketOutRemoveServer(ServerInfo serverInfo) {
        super(PacketRC.CN_WRAPPER + 5, new Document("serverInfo", serverInfo));
    }
}
