/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.UUID;

/**
 * Created by Tareko on 18.08.2017.
 */
public class PacketOutLoginPlayer extends Packet {

    public PacketOutLoginPlayer(CloudPlayer cloudPlayer) {
        super(PacketRC.PLAYER_HANDLE + 1, new Document("player", cloudPlayer));
    }

    public PacketOutLoginPlayer(UUID uniqueId, CloudPlayer cloudPlayer, String reason) {
        super(uniqueId, PacketRC.PLAYER_HANDLE + 1, new Document("player", cloudPlayer).append("reason", reason));
    }
}
