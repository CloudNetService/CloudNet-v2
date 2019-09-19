package de.dytanic.cloudnet.api.network.packet.api;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;

public class PacketOutCopyDirectory extends Packet {

    public PacketOutCopyDirectory(ServerInfo serverInfo, String directory) {
        super(PacketRC.SERVER_HANDLE + 10, new Document("serverInfo", serverInfo).append("directory", directory));
    }


}
