/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

public class PacketOutInstallUpdate extends Packet {

    public PacketOutInstallUpdate(String url) {
        super(PacketRC.CN_CORE + 8, new Document("url", url));
    }
}
