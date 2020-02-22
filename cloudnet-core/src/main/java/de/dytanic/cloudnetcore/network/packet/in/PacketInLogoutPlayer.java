package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.ProxyServer;

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
