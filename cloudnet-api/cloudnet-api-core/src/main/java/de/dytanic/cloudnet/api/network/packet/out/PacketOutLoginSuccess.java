/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.UUID;

/**
 * Created by Tareko on 05.10.2017.
 */
public class PacketOutLoginSuccess extends Packet {

    public PacketOutLoginSuccess(UUID uniqueId) {
        super(PacketRC.PLAYER_HANDLE + 6, new Document("uniqueId", uniqueId));
    }
}
