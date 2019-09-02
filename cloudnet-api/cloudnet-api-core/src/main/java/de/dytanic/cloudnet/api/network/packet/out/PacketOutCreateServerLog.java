/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 04.10.2017.
 */
public class PacketOutCreateServerLog extends Packet {

    public PacketOutCreateServerLog(String randomString, String serverId) {
        super(PacketRC.CN_INTERNAL_CHANNELS + 1, new Document("random", randomString).append("serverId", serverId));
    }
}
