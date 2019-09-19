/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 27.08.2017.
 */
public class PacketOutUpdateOnlinePlayer extends Packet {

    public PacketOutUpdateOnlinePlayer(CloudPlayer cloudPlayer) {
        super(PacketRC.PLAYER_HANDLE + 5, new Document("player", cloudPlayer));
    }
}
