package eu.cloudnetservice.cloudnet.v2.master.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 26.07.2017.
 */
public class PacketOutServerRemove extends Packet {

    public PacketOutServerRemove(ServerInfo removed) {
        super(PacketRC.SERVER_HANDLE + 4, new Document("serverInfo", removed));
    }

}
