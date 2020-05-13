package eu.cloudnetservice.v2.master.network.packet.out;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.v2.lib.server.template.Template;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 28.08.2017.
 */
public class PacketOutCopyServer extends Packet {

    public PacketOutCopyServer(ServerInfo serverInfo) {
        super(PacketRC.CN_CORE + 10, new Document("serverInfo", serverInfo));
    }

    public PacketOutCopyServer(ServerInfo serverInfo, Template template) {
        super(PacketRC.CN_CORE + 10, new Document("serverInfo", serverInfo).append("template", template));
    }
}
