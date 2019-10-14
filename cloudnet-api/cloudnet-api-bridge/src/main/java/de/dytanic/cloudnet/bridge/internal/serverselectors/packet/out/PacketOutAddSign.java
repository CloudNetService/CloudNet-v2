/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.serverselectors.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 23.08.2017.
 */
public class PacketOutAddSign extends Packet {

    public PacketOutAddSign(Sign sign) {
        super(PacketRC.SERVER_SELECTORS + 1, new Document("sign", sign));
    }
}
