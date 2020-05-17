package eu.cloudnetservice.cloudnet.v2.api.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.DefaultType;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

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
