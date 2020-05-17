package eu.cloudnetservice.cloudnet.v2.api.network.packet.api.sync;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 31.08.2017.
 */
public class PacketAPIOutGetServer extends Packet {

    public PacketAPIOutGetServer(String server) {
        super(PacketRC.API + 8, new Document("server", server));
    }
}
