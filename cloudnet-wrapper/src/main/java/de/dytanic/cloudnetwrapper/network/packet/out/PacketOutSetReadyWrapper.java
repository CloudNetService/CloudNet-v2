/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

public class PacketOutSetReadyWrapper extends Packet {

    public PacketOutSetReadyWrapper(boolean ready) {
        super(PacketRC.CN_WRAPPER + 7, new Document("ready", ready));
    }
}
