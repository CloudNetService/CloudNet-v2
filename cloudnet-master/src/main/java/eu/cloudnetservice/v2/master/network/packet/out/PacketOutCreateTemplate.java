package eu.cloudnetservice.v2.master.network.packet.out;

import eu.cloudnetservice.v2.lib.DefaultType;
import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.server.ProxyGroup;
import eu.cloudnetservice.v2.lib.server.ServerGroup;
import eu.cloudnetservice.v2.lib.utility.document.Document;

public class PacketOutCreateTemplate extends Packet {

    public PacketOutCreateTemplate(ServerGroup serverGroup) {
        super(PacketRC.CN_CORE + 5, new Document("serverGroup", serverGroup).append("type", DefaultType.BUKKIT.name()));
    }

    public PacketOutCreateTemplate(ProxyGroup serverGroup) {
        super(PacketRC.CN_CORE + 5, new Document("proxyGroup", serverGroup).append("type", DefaultType.BUNGEE_CORD.name()));
    }
}
