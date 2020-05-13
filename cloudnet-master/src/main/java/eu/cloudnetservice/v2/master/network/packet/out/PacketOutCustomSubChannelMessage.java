package eu.cloudnetservice.v2.master.network.packet.out;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 27.08.2017.
 */
public class PacketOutCustomSubChannelMessage extends Packet {
    public PacketOutCustomSubChannelMessage(String channel, String message, Document document) {
        super(PacketRC.SERVER_HANDLE + 9, new Document("channel", channel).append("message", message).append("value", document));
    }
}
