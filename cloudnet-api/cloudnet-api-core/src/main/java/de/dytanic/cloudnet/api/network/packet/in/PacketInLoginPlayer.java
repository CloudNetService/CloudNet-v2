package de.dytanic.cloudnet.api.network.packet.in;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.network.packet.PacketInHandlerDefault;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.player.CloudPlayer;

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
