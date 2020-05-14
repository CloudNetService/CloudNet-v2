package eu.cloudnetservice.cloudnet.v2.master.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 23.07.2017.
 */
public class PacketOutUpdateServerInfo extends Packet {

    public PacketOutUpdateServerInfo(ServerInfo serverInfo) {
        super(PacketRC.SERVER_HANDLE + 3, new Document("serverInfo", serverInfo));
    }
}
