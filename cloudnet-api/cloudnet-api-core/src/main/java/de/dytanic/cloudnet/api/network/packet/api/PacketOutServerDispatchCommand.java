/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.api;

import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketOutServerDispatchCommand extends Packet {

    public PacketOutServerDispatchCommand(DefaultType defaultType, String serverId, String commandLine) {
        super(PacketRC.CN_CORE + 5, new Document("defaultType", defaultType).append("serverId", serverId)
                                                                            .append("commandLine", commandLine));
    }
}
