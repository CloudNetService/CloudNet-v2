package eu.cloudnetservice.cloudnet.v2.master.network.packet.in;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;

public class PacketInDispatchConsoleMessage implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        System.out.println(packet.getData().getString("output"));
    }
}
