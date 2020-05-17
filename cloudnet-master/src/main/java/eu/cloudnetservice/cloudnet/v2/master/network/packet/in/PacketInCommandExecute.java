package eu.cloudnetservice.cloudnet.v2.master.network.packet.in;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.player.PlayerCommandExecution;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;

/**
 * Created by Tareko on 23.07.2017.
 */
public class PacketInCommandExecute implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        PlayerCommandExecution playerCommandExecutor = packet.getData().getObject("playerCommandExecution", PlayerCommandExecution.class);
        CloudNet.getInstance().getNetworkManager().handleCommandExecute(playerCommandExecutor);
    }
}
