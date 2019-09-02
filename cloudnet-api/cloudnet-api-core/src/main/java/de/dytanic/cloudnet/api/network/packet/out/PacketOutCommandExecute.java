/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.player.PlayerCommandExecution;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketOutCommandExecute extends Packet {

    public PacketOutCommandExecute(PlayerCommandExecution playerCommandExecution) {
        super(PacketRC.PLAYER_HANDLE + 4, new Document("playerCommandExecution", playerCommandExecution));
    }
}
