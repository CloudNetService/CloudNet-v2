package eu.cloudnetservice.v2.master.network.packet.in;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.v2.master.CloudNet;

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
