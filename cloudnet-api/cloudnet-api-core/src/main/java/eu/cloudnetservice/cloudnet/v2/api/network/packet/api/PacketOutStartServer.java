package eu.cloudnetservice.cloudnet.v2.api.network.packet.api;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.process.ServerProcessData;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketOutStartServer extends Packet {

    public static final int PACKET_ID = PacketRC.SERVER_HANDLE + 4;

    public PacketOutStartServer(final ServerProcessData serverProcessData) {
        super(PACKET_ID, new Document("serverProcess", serverProcessData));
    }
}
