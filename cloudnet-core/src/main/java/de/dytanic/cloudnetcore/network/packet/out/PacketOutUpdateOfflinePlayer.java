package de.dytanic.cloudnetcore.network.packet.out;


import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.utility.document.Document;

public class PacketOutUpdateOfflinePlayer extends Packet {

    public PacketOutUpdateOfflinePlayer(OfflinePlayer offlinePlayer) {
        super(PacketRC.PLAYER_HANDLE + 5, new Document("player", offlinePlayer));
    }
}
