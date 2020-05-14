package eu.cloudnetservice.cloudnet.v2.api.network.packet.api;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketOutUpdateServerGroup extends Packet {

    public PacketOutUpdateServerGroup(ServerGroup serverGroup) {
        super(PacketRC.CN_CORE + 2, new Document("group", serverGroup));
    }
}
