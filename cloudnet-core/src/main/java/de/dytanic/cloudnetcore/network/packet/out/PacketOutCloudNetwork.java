/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

public class PacketOutCloudNetwork extends Packet {

    public PacketOutCloudNetwork(CloudNetwork cloudNetwork) {
        super(PacketRC.SERVER_HANDLE + 1, new Document("cloudnetwork", cloudNetwork));
    }
}
