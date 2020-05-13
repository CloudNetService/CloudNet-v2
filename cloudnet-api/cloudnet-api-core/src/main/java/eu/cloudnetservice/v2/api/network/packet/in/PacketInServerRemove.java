package eu.cloudnetservice.v2.api.network.packet.in;

import eu.cloudnetservice.v2.api.CloudAPI;
import eu.cloudnetservice.v2.api.network.packet.PacketInHandlerDefault;
import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.lib.server.info.ServerInfo;

/**
 * Created by Tareko on 17.08.2017.
 */
public class PacketInServerRemove implements PacketInHandlerDefault {
    public void handleInput(Packet packet, PacketSender packetSender) {
        ServerInfo serverInfo = packet.getData().getObject("serverInfo", ServerInfo.TYPE);
        if (CloudAPI.getInstance() != null) {
            CloudAPI.getInstance().getNetworkHandlerProvider().iterator(
                obj -> obj.onServerRemove(serverInfo));
        }
    }
}
