package eu.cloudnetservice.v2.master.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.components.ProxyServer;

import java.util.UUID;

/**
 * Created by Tareko on 20.07.2017.
 */
public class PacketInLogoutPlayer implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        CloudPlayer cloudPlayer = packet.getData().getObject("player", CloudPlayer.TYPE);
        if (cloudPlayer != null) {
            CloudNet.getInstance().getNetworkManager().handlePlayerLogout(cloudPlayer);
        } else if (packetSender instanceof ProxyServer) {
            CloudNet.getInstance().getNetworkManager().handlePlayerLogout(
                packet.getData().getObject("uniqueId", UUID.class),
                (ProxyServer) packetSender);
        }
    }
}
