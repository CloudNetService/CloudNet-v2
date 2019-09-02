/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 31.08.2017.
 */
public class PacketAPIOutGetServer extends Packet {

    public PacketAPIOutGetServer(String server) {
        super(PacketRC.API + 8, new Document("server", server));
    }
}
