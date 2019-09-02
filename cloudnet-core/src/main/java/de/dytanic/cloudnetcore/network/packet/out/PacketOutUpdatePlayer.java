/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 18.08.2017.
 */
public class PacketOutUpdatePlayer extends Packet {

    public PacketOutUpdatePlayer(CloudPlayer cloudPlayer) {
        super(PacketRC.PLAYER_HANDLE + 3, new Document("player", cloudPlayer));
    }
}
