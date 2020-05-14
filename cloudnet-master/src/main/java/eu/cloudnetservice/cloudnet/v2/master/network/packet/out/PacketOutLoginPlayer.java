package eu.cloudnetservice.cloudnet.v2.master.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.util.UUID;

/**
 * Created by Tareko on 18.08.2017.
 */
public class PacketOutLoginPlayer extends Packet {

    public PacketOutLoginPlayer(CloudPlayer cloudPlayer) {
        super(PacketRC.PLAYER_HANDLE + 1, new Document("player", cloudPlayer));
    }

    public PacketOutLoginPlayer(UUID uniqueId, CloudPlayer cloudPlayer, String reason) {
        super(uniqueId, PacketRC.PLAYER_HANDLE + 1, new Document("player", cloudPlayer).append("reason", reason));
    }
}
