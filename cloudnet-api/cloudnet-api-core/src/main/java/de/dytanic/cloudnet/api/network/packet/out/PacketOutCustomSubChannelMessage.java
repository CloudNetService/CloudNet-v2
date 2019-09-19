/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.out;

import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 24.08.2017.
 */
public class PacketOutCustomSubChannelMessage extends Packet {

    public PacketOutCustomSubChannelMessage(DefaultType defaultType, String channel, String message, Document value) {
        super(PacketRC.SERVER_HANDLE + 8, new Document("defaultType", defaultType).append("channel", channel)
                                                                                  .append("message", message)
                                                                                  .append("value", value));
    }

    public PacketOutCustomSubChannelMessage(DefaultType defaultType, String serverId, String channel, String message, Document value) {
        super(PacketRC.SERVER_HANDLE + 8, new Document("defaultType", defaultType).append("serverId", serverId)
                                                                                  .append("channel", channel)
                                                                                  .append("message", message)
                                                                                  .append("value", value));
    }

}
