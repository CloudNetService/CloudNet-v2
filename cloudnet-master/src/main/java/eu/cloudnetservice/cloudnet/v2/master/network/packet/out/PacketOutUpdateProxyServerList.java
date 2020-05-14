package eu.cloudnetservice.cloudnet.v2.master.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.SimpleServerInfo;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.util.HashMap;
import java.util.Map;

public class PacketOutUpdateProxyServerList extends Packet {

    public PacketOutUpdateProxyServerList(Map<String, ServerInfo> stringServerInfoMap) {
        super(PacketRC.SERVER_HANDLE + 1, new Document("servers", getSimpleServerMap(stringServerInfoMap)));
    }

    private static Object getSimpleServerMap(final Map<String, ServerInfo> stringServerInfoMap) {
        final Map<String, SimpleServerInfo> simpleServerMap = new HashMap<>();
        stringServerInfoMap.forEach(
            (serverId, serverInfo) -> simpleServerMap.put(serverId, serverInfo.toSimple()));
        return simpleServerMap;
    }
}
