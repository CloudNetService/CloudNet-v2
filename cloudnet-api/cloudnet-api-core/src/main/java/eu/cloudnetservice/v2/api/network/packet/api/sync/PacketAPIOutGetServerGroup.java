package eu.cloudnetservice.v2.api.network.packet.api.sync;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 18.08.2017.
 */
public class PacketAPIOutGetServerGroup extends Packet {

    public PacketAPIOutGetServerGroup(String name) {
        super(PacketRC.API + 6, new Document("serverGroup", name));
    }
}
