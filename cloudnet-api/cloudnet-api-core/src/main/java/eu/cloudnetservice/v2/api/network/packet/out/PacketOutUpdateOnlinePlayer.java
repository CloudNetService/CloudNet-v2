package eu.cloudnetservice.v2.api.network.packet.out;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 27.08.2017.
 */
public class PacketOutUpdateOnlinePlayer extends Packet {

    public PacketOutUpdateOnlinePlayer(CloudPlayer cloudPlayer) {
        super(PacketRC.PLAYER_HANDLE + 5, new Document("player", cloudPlayer));
    }
}
