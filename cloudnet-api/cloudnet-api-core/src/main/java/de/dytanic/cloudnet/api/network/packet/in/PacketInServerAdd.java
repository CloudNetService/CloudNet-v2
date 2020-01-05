package de.dytanic.cloudnet.api.network.packet.in;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.network.packet.PacketInHandlerDefault;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;

/**
 * Created by Tareko on 17.08.2017.
 */
public class PacketInServerAdd implements PacketInHandlerDefault {

    public void handleInput(Packet packet, PacketSender packetSender) {
        ServerInfo serverInfo = packet.getData().getObject("serverInfo", ServerInfo.TYPE);
        if (CloudAPI.getInstance() != null) {
            CloudAPI.getInstance().getNetworkHandlerProvider().iterator(
                obj -> obj.onServerAdd(serverInfo));
        }
    }
}
