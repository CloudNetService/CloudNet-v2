/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;

public class PacketOutOnlineServer extends Packet {

    public PacketOutOnlineServer(ServerInfo serverInfo) {
        super(PacketRC.CN_CORE + 11, new Document("serverInfo", serverInfo));
    }
}
