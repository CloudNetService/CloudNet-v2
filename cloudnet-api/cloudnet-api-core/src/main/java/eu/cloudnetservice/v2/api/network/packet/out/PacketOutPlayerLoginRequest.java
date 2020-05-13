package eu.cloudnetservice.v2.api.network.packet.out;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.player.PlayerConnection;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 18.08.2017.
 */
public class PacketOutPlayerLoginRequest extends Packet {

    public PacketOutPlayerLoginRequest(PlayerConnection playerConnection) {
        super(PacketRC.PLAYER_HANDLE + 1, new Document("playerConnection", playerConnection));
    }
}
