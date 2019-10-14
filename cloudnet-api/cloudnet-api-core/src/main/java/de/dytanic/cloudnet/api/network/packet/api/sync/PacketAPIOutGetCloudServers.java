/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 25.10.2017.
 */
public class PacketAPIOutGetCloudServers extends Packet {

    public PacketAPIOutGetCloudServers() {
        super(PacketRC.API + 9, new Document());
    }
}
