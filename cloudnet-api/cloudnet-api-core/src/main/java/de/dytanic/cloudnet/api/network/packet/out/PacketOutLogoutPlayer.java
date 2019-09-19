/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.UUID;

/**
 * Created by Tareko on 20.08.2017.
 */
public class PacketOutLogoutPlayer extends Packet {

    public PacketOutLogoutPlayer(CloudPlayer cloudPlayer, UUID uniqueId) {
        super(PacketRC.PLAYER_HANDLE + 3, new Document("player", cloudPlayer).append("uniqueId", uniqueId));
    }
}
