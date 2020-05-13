package eu.cloudnetservice.v2.master.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.player.PlayerCommandExecution;
import eu.cloudnetservice.v2.master.CloudNet;

/**
 * Created by Tareko on 23.07.2017.
 */
public class PacketInCommandExecute implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        PlayerCommandExecution playerCommandExecutor = packet.getData().getObject("playerCommandExecution", PlayerCommandExecution.class);
        CloudNet.getInstance().getNetworkManager().handleCommandExecute(playerCommandExecutor);
    }
}
