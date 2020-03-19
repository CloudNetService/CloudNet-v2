package de.dytanic.cloudnet.api.network.packet.api;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.process.ServerProcessData;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketOutStartServer extends Packet {

    public static final int PACKET_ID = PacketRC.SERVER_HANDLE + 4;

    public PacketOutStartServer(final ServerProcessData serverProcessData) {
        super(PACKET_ID, new Document("serverProcess", serverProcessData));
    }
}
