package eu.cloudnetservice.cloudnet.v2.api.network.packet.in;

import eu.cloudnetservice.cloudnet.v2.api.CloudAPI;
import eu.cloudnetservice.cloudnet.v2.api.network.packet.PacketInHandlerDefault;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.player.CloudPlayer;

/**
 * Created by Tareko on 18.08.2017.
 */
public class PacketInUpdatePlayer implements PacketInHandlerDefault {

    public void handleInput(Packet packet, PacketSender packetSender) {
        CloudPlayer cloudPlayer = packet.getData().getObject("player", CloudPlayer.TYPE);
        if (CloudAPI.getInstance() != null) {
            CloudAPI.getInstance().getNetworkHandlerProvider().iterator(
                obj -> obj.onPlayerUpdate(cloudPlayer));
        }
    }
}
