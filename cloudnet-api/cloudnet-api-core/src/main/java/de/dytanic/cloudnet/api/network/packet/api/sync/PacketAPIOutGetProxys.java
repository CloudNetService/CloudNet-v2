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
public class PacketAPIOutGetProxys extends Packet {

    public PacketAPIOutGetProxys() {
        super(PacketRC.API + 4, new Document());
    }

    public PacketAPIOutGetProxys(String group) {
        super(PacketRC.API + 4, new Document("group", group));
    }
}
