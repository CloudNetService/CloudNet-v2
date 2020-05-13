package eu.cloudnetservice.v2.master.network.packet.in;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.master.CloudNet;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketInCustomChannelMessage implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        CloudNet.getInstance().getNetworkManager().handleCustomChannelMessage(
            packet.getData().getString("channel"),
            packet.getData().getString("message"),
            packet.getData().getDocument("value"),
            packetSender);
    }
}
