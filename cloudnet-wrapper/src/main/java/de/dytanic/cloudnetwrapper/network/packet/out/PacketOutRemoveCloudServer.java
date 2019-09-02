/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 17.10.2017.
 */
public class PacketOutRemoveCloudServer extends Packet {

    public PacketOutRemoveCloudServer(ServerInfo serverInfo) {
        super(PacketRC.CN_WRAPPER + 14, new Document("serverInfo", serverInfo));
    }
}
