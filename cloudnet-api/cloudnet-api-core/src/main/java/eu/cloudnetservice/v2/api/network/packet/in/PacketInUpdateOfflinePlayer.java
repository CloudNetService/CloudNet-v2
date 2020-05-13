package eu.cloudnetservice.v2.api.network.packet.in;

import eu.cloudnetservice.v2.api.CloudAPI;
import eu.cloudnetservice.v2.api.network.packet.PacketInHandlerDefault;
import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.lib.player.OfflinePlayer;

public final class PacketInUpdateOfflinePlayer implements PacketInHandlerDefault {

    public void handleInput(Packet packet, PacketSender packetSender) {
        OfflinePlayer offlinePlayer = packet.getData().getObject("player", OfflinePlayer.TYPE);

        if (CloudAPI.getInstance() != null) {
            CloudAPI.getInstance().getNetworkHandlerProvider().iterator(obj -> obj.onOfflinePlayerUpdate(offlinePlayer));
        }
    }
}
