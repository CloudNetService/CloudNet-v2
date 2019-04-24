/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.player.PlayerConnection;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 18.08.2017.
 */
public class PacketOutPlayerLoginRequest extends Packet {

    public PacketOutPlayerLoginRequest(PlayerConnection playerConnection) {
        super(PacketRC.PLAYER_HANDLE + 1, new Document("playerConnection", playerConnection));
    }
}