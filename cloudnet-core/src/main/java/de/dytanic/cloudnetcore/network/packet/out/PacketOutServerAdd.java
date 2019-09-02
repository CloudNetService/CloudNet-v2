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
public class PacketOutServerAdd extends Packet {

    public PacketOutServerAdd(ServerInfo add) {
        super(PacketRC.SERVER_HANDLE + 2, new Document().append("serverInfo", add));
    }
}
