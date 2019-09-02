/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 18.08.2017.
 */
public class PacketOutUpdatePlayer extends Packet {

    public PacketOutUpdatePlayer(OfflinePlayer offlinePlayer) {
        super(PacketRC.PLAYER_HANDLE + 2, new Document("player", offlinePlayer));
    }
}
