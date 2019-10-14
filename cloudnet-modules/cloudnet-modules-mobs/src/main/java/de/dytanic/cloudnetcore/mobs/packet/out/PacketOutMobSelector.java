/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.mobs.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobConfig;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Tareko on 01.09.2017.
 */
public final class PacketOutMobSelector extends Packet {

    public PacketOutMobSelector(MobConfig mobConfig, Map<UUID, ServerMob> mobs) {
        super(PacketRC.SERVER_SELECTORS + 2, new Document("mobConfig", mobConfig).append("mobs", mobs));
    }
}
