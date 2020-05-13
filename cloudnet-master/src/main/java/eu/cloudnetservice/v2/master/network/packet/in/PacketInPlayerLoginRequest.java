package eu.cloudnetservice.v2.master.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.player.PlayerConnection;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.components.ProxyServer;

public final class PacketInPlayerLoginRequest implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (packetSender instanceof ProxyServer && packet.getUniqueId() != null) {
            PlayerConnection playerConnection = packet.getData().getObject("playerConnection", PlayerConnection.TYPE);
            CloudNet.getInstance().getNetworkManager().handlePlayerLoginRequest(
                ((ProxyServer) packetSender),
                playerConnection,
                packet.getUniqueId());
        }

    }
}
