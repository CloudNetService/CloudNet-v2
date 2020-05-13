package eu.cloudnetservice.v2.api.network.packet.in;

import eu.cloudnetservice.v2.api.CloudAPI;
import eu.cloudnetservice.v2.api.network.packet.PacketInHandlerDefault;
import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;

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
