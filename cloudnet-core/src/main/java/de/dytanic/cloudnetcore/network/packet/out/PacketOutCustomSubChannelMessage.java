/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 27.08.2017.
 */
public class PacketOutCustomSubChannelMessage extends Packet {
    public PacketOutCustomSubChannelMessage(String channel, String message, Document document) {
        super(PacketRC.SERVER_HANDLE + 9, new Document("channel", channel).append("message", message).append("value", document));
    }
}
