package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnetcore.CloudNet;

import java.util.UUID;

/**
 * Created by Tareko on 05.10.2017.
 */
public class PacketInLoginSuccess implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        UUID unique = packet.getData().getObject("uniqueId", UUID.class);
        CloudPlayer cloudPlayer = CloudNet.getInstance().getNetworkManager().getWaitingPlayers().get(unique);
        if (cloudPlayer != null) {
            CloudNet.getInstance().getNetworkManager().getWaitingPlayers().remove(unique);
            CloudNet.getInstance().getNetworkManager().getOnlinePlayers().put(cloudPlayer.getUniqueId(), cloudPlayer);
        }
    }
}
