/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 28.08.2017.
 */
public class PacketOutUpdateCPUUsage extends Packet {

    public PacketOutUpdateCPUUsage(double cpuUsage) {
        super(PacketRC.CN_WRAPPER + 11, new Document().append("cpuUsage", cpuUsage));
    }
}
