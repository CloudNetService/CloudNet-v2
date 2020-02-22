package de.dytanic.cloudnet.api.network.packet.in;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.network.packet.PacketInHandlerDefault;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;

/**
 * Created by Tareko on 24.08.2017.
 */
public class PacketInCustomSubChannelMessage implements PacketInHandlerDefault {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (CloudAPI.getInstance() != null) {
            CloudAPI.getInstance().getNetworkHandlerProvider().iterator(
                obj -> obj.onCustomSubChannelMessageReceive(
                    packet.getData().getString("channel"),
                    packet.getData().getString("message"),
                    packet.getData().getDocument("value")));
        }
    }
}
