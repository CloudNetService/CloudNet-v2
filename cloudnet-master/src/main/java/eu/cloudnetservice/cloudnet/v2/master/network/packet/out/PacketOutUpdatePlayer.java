package eu.cloudnetservice.cloudnet.v2.master.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 18.08.2017.
 */
public class PacketOutUpdatePlayer extends Packet {

    public PacketOutUpdatePlayer(CloudPlayer cloudPlayer) {
        super(PacketRC.PLAYER_HANDLE + 3, new Document("player", cloudPlayer));
    }
}
