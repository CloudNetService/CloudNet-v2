package eu.cloudnetservice.cloudnet.v2.api.network.packet.api;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketOutStopServer extends Packet {

    public PacketOutStopServer(String serverId) {
        super(PacketRC.SERVER_HANDLE + 5, new Document("serverId", serverId));
    }
}
