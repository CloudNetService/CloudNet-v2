/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketOutCustomChannelMessage extends Packet {

    public PacketOutCustomChannelMessage(String channel, String message, Document value) {
        super(PacketRC.SERVER_HANDLE + 4, new Document("channel", channel).append("message", message).append("value", value));
    }
}