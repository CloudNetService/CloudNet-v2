package eu.cloudnetservice.cloudnet.v2.api.network.packet.in;

import eu.cloudnetservice.cloudnet.v2.api.CloudAPI;
import eu.cloudnetservice.cloudnet.v2.api.network.packet.PacketInHandlerDefault;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;

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
