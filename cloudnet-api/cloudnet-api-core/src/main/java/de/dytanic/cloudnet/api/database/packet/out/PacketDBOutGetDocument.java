/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.database.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 24.08.2017.
 */
public class PacketDBOutGetDocument extends Packet {

    public PacketDBOutGetDocument(String name, String db) {
        super(PacketRC.DB + 1, new Document("name", name).append("db", db));
    }

    public PacketDBOutGetDocument(String db) {
        super(PacketRC.DB + 1, new Document("all", true).append("db", db));
    }
}
