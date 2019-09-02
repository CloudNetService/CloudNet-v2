/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;
import net.md_5.bungee.config.Configuration;

/**
 * Created by Tareko on 31.08.2017.
 */
public class PacketOutUpdateWrapperProperties extends Packet {

    public PacketOutUpdateWrapperProperties(Configuration properties) {
        super(PacketRC.CN_CORE + 12, new Document("properties", properties));
    }
}
