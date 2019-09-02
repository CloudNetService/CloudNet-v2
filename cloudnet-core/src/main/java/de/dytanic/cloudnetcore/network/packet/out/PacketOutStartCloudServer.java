/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.cloudserver.CloudServerMeta;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 22.10.2017.
 */
public class PacketOutStartCloudServer extends Packet {

    public PacketOutStartCloudServer(CloudServerMeta cloudServerMeta) {
        super(PacketRC.CN_CORE + 13, new Document("cloudServerMeta", cloudServerMeta));
    }

    public PacketOutStartCloudServer(CloudServerMeta cloudServerMeta, boolean async) {
        super(PacketRC.CN_CORE + 13, new Document("cloudServerMeta", cloudServerMeta).append("async", async));
    }
}
