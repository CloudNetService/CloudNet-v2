/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.serverselectors.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 25.08.2017.
 */
public class PacketOutAddMob extends Packet {

    public PacketOutAddMob(ServerMob mob) {
        super(PacketRC.SERVER_SELECTORS + 3, new Document("mob", mob));
    }
}
