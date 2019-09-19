/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.ServerProcessMeta;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;

public class PacketOutAddServer extends Packet {

    public PacketOutAddServer(ServerInfo serverInfo, ServerProcessMeta serverProcessMeta) {
        super(PacketRC.CN_WRAPPER + 2, new Document("serverInfo", serverInfo).append("serverProcess", serverProcessMeta));
    }
}
