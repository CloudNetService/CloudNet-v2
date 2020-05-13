package eu.cloudnetservice.v2.api.network.packet.api.sync;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 17.08.2017.
 */
public class PacketAPIOutGetServers extends Packet {

    public PacketAPIOutGetServers() {
        super(PacketRC.API + 3, new Document());
    }

    public PacketAPIOutGetServers(String group) {
        super(PacketRC.API + 3, new Document("group", group));
    }
}
