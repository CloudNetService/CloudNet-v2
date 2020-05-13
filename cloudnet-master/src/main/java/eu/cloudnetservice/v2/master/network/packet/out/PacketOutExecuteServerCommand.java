package eu.cloudnetservice.v2.master.network.packet.out;

import eu.cloudnetservice.v2.lib.DefaultType;
import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.v2.lib.utility.document.Document;

public class PacketOutExecuteServerCommand extends Packet {
    public PacketOutExecuteServerCommand(ServerInfo serverInfo, String commandLine) {
        super(PacketRC.CN_CORE + 7, new Document("serverInfo", serverInfo).append("type", DefaultType.BUKKIT)
                                                                          .append("commandLine", commandLine));
    }

    public PacketOutExecuteServerCommand(ProxyInfo serverInfo, String commandLine) {
        super(PacketRC.CN_CORE + 7, new Document("proxyInfo", serverInfo).append("type", DefaultType.BUNGEE_CORD)
                                                                         .append("commandLine", commandLine));
    }
}
