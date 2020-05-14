package eu.cloudnetservice.cloudnet.v2.master.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 30.07.2017.
 */
public class PacketOutStopServer extends Packet {
    public PacketOutStopServer(ServerInfo serviceId) {
        super(PacketRC.CN_CORE + 4, new Document("serverInfo", serviceId));
    }
}
