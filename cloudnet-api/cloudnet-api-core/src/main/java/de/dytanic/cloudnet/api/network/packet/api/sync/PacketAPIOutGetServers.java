/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 17.08.2017.
 */
public class PacketAPIOutGetServers extends Packet {

    public PacketAPIOutGetServers() {
        super(PacketRC.API + 3, new Document());
    }

    public PacketAPIOutGetServers(String group) {
        super(PacketRC.API + 3, new Document("group", group));
    }
}
