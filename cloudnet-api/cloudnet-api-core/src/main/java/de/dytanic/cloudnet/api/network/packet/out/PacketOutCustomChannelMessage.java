/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 18.08.2017.
 */
public class PacketOutCustomChannelMessage extends Packet {

    public PacketOutCustomChannelMessage(String channel, String message, Document value) {
        super(PacketRC.SERVER_HANDLE + 3, new Document("message", message).append("value", value).append("channel", channel));
    }
}
