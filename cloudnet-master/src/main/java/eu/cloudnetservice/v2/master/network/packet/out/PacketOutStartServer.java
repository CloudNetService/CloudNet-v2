package eu.cloudnetservice.v2.master.network.packet.out;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.server.ServerProcessMeta;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 30.07.2017.
 */
public class PacketOutStartServer extends Packet {
    public PacketOutStartServer(ServerProcessMeta serverProcessMeta) {
        super(PacketRC.CN_CORE + 3, new Document("serverProcess", serverProcessMeta));
    }

}
