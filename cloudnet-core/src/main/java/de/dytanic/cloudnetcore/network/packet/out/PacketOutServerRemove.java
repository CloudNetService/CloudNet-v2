/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 26.07.2017.
 */
public class PacketOutServerRemove extends Packet {

    public PacketOutServerRemove(ServerInfo removed) {
        super(PacketRC.SERVER_HANDLE + 4, new Document("serverInfo", removed));
    }

}
