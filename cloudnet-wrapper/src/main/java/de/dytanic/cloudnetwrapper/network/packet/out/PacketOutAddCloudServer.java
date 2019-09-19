/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.out;

import de.dytanic.cloudnet.lib.cloudserver.CloudServerMeta;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 17.10.2017.
 */
public class PacketOutAddCloudServer extends Packet {

    public PacketOutAddCloudServer(ServerInfo serverInfo, CloudServerMeta cloudServerMeta) {
        super(PacketRC.CN_WRAPPER + 13, new Document("serverInfo", serverInfo).append("cloudServerMeta", cloudServerMeta));
    }
}
