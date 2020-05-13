package eu.cloudnetservice.v2.api.network.packet.out;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.player.PlayerCommandExecution;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketOutCommandExecute extends Packet {

    public PacketOutCommandExecute(PlayerCommandExecution playerCommandExecution) {
        super(PacketRC.PLAYER_HANDLE + 4, new Document("playerCommandExecution", playerCommandExecution));
    }
}
