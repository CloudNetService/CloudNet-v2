package eu.cloudnetservice.v2.master.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import eu.cloudnetservice.v2.master.CloudNet;

/**
 * Created by Tareko on 27.08.2017.
 */
public class PacketInUpdateOnlinePlayer implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        CloudNet.getInstance().getNetworkManager().handlePlayerUpdate(packet.getData().getObject("player", CloudPlayer.TYPE));
    }
}
