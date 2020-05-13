package eu.cloudnetservice.v2.master.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;

public class PacketInDispatchConsoleMessage implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        System.out.println(packet.getData().getString("output"));
    }
}
