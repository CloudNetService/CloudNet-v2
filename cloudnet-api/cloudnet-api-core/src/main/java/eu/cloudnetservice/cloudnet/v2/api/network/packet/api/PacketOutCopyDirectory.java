package eu.cloudnetservice.cloudnet.v2.api.network.packet.api;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

public class PacketOutCopyDirectory extends Packet {

    public PacketOutCopyDirectory(ServerInfo serverInfo, String directory) {
        super(PacketRC.SERVER_HANDLE + 10, new Document("serverInfo", serverInfo).append("directory", directory));
    }


}
