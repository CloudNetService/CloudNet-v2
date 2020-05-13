package eu.cloudnetservice.v2.api.network.packet.in;

import eu.cloudnetservice.v2.api.CloudAPI;
import eu.cloudnetservice.v2.api.network.packet.PacketInHandlerDefault;
import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.lib.player.CloudPlayer;

/**
 * Created by Tareko on 18.08.2017.
 */
public class PacketInLoginPlayer implements PacketInHandlerDefault {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (CloudAPI.getInstance() != null) {
            CloudPlayer cloudPlayer = packet.getData().getObject("player", CloudPlayer.TYPE);
            CloudAPI.getInstance().getNetworkHandlerProvider().iterator(obj -> obj.onPlayerLoginNetwork(cloudPlayer));
        }
    }
}
