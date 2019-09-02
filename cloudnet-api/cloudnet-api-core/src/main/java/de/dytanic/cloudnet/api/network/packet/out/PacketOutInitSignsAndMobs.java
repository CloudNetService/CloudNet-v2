/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobConfig;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignLayoutConfig;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketOutInitSignsAndMobs extends Packet {

    public PacketOutInitSignsAndMobs(SignLayoutConfig signLayoutConfig, MobConfig mobConfig, Map<UUID, Sign> signs) {
        super(PacketRC.CN_CORE + 1, new Document("signLayout", signLayoutConfig).append("mobConfig", mobConfig).append("signs", signs));
    }
}
