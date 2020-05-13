package eu.cloudnetservice.v2.master.network.packet.out;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketOutCustomChannelMessage extends Packet {

    public PacketOutCustomChannelMessage(String channel, String message, Document value) {
        super(PacketRC.SERVER_HANDLE + 4, new Document("channel", channel).append("message", message).append("value", value));
    }
}
