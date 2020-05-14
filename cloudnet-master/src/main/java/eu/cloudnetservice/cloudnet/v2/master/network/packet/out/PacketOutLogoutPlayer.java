package eu.cloudnetservice.cloudnet.v2.master.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.util.UUID;

/**
 * Created by Tareko on 18.08.2017.
 */
public class PacketOutLogoutPlayer extends Packet {

    public PacketOutLogoutPlayer(CloudPlayer cloudPlayer) {
        super(PacketRC.PLAYER_HANDLE + 2, new Document("player", cloudPlayer));
    }

    public PacketOutLogoutPlayer(UUID uniqueId) {
        super(PacketRC.PLAYER_HANDLE + 2, new Document("uniqueId", uniqueId));
    }
}
