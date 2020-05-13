package eu.cloudnetservice.v2.master.network.packet.api;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.master.CloudNet;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInExecuteCommand implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        String commandLine = packet.getData().getString("command");
        if (commandLine != null) {
            CloudNet.getInstance().getCommandManager().dispatchCommand(commandLine);
        }
    }
}