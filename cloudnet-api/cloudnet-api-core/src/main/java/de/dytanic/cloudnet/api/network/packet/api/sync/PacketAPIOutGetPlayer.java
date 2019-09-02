/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.UUID;

/**
 * Created by Tareko on 17.08.2017.
 */
public class PacketAPIOutGetPlayer extends Packet {

    public PacketAPIOutGetPlayer(UUID uniqueId) {
        super(PacketRC.API + 1, new Document("uniqueId", uniqueId));
    }
}
