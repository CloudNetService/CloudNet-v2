package de.dytanic.cloudnet.api.network.packet.in;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.network.packet.PacketInHandlerDefault;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;

/**
 * Created by Tareko on 18.08.2017.
 */
public class PacketInUpdateOnlineCount implements PacketInHandlerDefault {

    public void handleInput(Packet packet, PacketSender packetSender) {
        int online = packet.getData().getInt("onlineCount");
        CloudAPI.getInstance().getCloudNetwork().setOnlineCount(online);
        CloudAPI.getInstance().getNetworkHandlerProvider().iterator(
            obj -> obj.onUpdateOnlineCount(online));
    }
}
