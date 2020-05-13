package eu.cloudnetservice.v2.api.network.packet.api;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketOutStopProxy extends Packet {

    public PacketOutStopProxy(String serverId) {
        super(PacketRC.SERVER_HANDLE + 7, new Document("serverId", serverId));
    }

}
