package eu.cloudnetservice.cloudnet.v2.api.network.packet.in;

import eu.cloudnetservice.cloudnet.v2.api.CloudAPI;
import eu.cloudnetservice.cloudnet.v2.api.network.packet.PacketInHandlerDefault;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.player.CloudPlayer;

import java.util.UUID;

/**
 * Created by Tareko on 18.08.2017.
 */
public final class PacketInLogoutPlayer implements PacketInHandlerDefault {

    public void handleInput(Packet packet, PacketSender packetSender) {
        CloudPlayer cloudPlayer = packet.getData().getObject("player", CloudPlayer.TYPE);

        if (cloudPlayer != null) {
            if (CloudAPI.getInstance() != null) {
                CloudAPI.getInstance().getNetworkHandlerProvider().iterator(
                    obj -> obj.onPlayerDisconnectNetwork(cloudPlayer));
            }
        } else {
            UUID uuid = packet.getData().getObject("uniqueId", UUID.class);
            if (CloudAPI.getInstance() != null) {
                CloudAPI.getInstance().getNetworkHandlerProvider().iterator(
                    obj -> obj.onPlayerDisconnectNetwork(uuid));
            }
        }
    }
}
