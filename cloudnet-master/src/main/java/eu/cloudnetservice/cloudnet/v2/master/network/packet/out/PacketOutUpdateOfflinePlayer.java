package eu.cloudnetservice.cloudnet.v2.master.network.packet.out;


import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.player.OfflinePlayer;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

public class PacketOutUpdateOfflinePlayer extends Packet {

    public PacketOutUpdateOfflinePlayer(OfflinePlayer offlinePlayer) {
        super(PacketRC.PLAYER_HANDLE + 5, new Document("player", offlinePlayer));
    }
}
