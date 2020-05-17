package eu.cloudnetservice.cloudnet.v2.master.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketOutUpdateOnlineCount extends Packet {

    public PacketOutUpdateOnlineCount(int onlineCount) {
        super(PacketRC.PLAYER_HANDLE + 4, new Document("onlineCount", onlineCount));
    }
}
