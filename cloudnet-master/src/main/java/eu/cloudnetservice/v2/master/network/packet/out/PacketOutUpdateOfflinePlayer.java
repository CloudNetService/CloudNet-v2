package eu.cloudnetservice.v2.master.network.packet.out;


import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.player.OfflinePlayer;
import eu.cloudnetservice.v2.lib.utility.document.Document;

public class PacketOutUpdateOfflinePlayer extends Packet {

    public PacketOutUpdateOfflinePlayer(OfflinePlayer offlinePlayer) {
        super(PacketRC.PLAYER_HANDLE + 5, new Document("player", offlinePlayer));
    }
}
