package eu.cloudnetservice.v2.wrapper.network.packet.in;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.wrapper.CloudNetWrapper;

public class PacketInExecuteCommand implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        String commandLine = packet.getData().getString("commandLine");
        CloudNetWrapper.getInstance().getCommandManager().dispatchCommand(commandLine);
    }
}
