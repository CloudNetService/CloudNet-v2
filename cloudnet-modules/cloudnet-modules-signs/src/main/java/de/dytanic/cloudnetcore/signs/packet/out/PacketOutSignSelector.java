/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.signs.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignLayoutConfig;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Tareko on 23.08.2017.
 */
public class PacketOutSignSelector extends Packet {

    public PacketOutSignSelector(Map<UUID, Sign> signMap, SignLayoutConfig signLayoutConfig) {
        super(PacketRC.SERVER_SELECTORS + 1, new Document("signs", signMap).append("signLayoutConfig", signLayoutConfig));
    }
}
