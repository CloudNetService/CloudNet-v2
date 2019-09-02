/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 18.08.2017.
 */
public class PacketAPIOutGetServerGroup extends Packet {

    public PacketAPIOutGetServerGroup(String name) {
        super(PacketRC.API + 6, new Document("serverGroup", name));
    }
}
