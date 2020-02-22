package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnetcore.CloudNet;

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
