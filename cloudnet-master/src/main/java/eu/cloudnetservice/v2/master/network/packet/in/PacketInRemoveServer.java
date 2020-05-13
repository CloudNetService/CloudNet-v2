package eu.cloudnetservice.v2.master.network.packet.in;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.v2.master.network.components.Wrapper;

/**
 * Created by Tareko on 20.07.2017.
 */
public class PacketInRemoveServer implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }

        Wrapper wrapper = (Wrapper) packetSender;
        ServerInfo serverInfo = packet.getData().getObject("serverInfo", ServerInfo.TYPE);

        if (wrapper.getServers().containsKey(serverInfo.getServiceId().getServerId())) {
            MinecraftServer minecraftServer = wrapper.getServers().get(serverInfo.getServiceId().getServerId());
            if (minecraftServer.getChannel() != null) {
                minecraftServer.getChannel().close();
            }

            wrapper.getServers().remove(serverInfo.getServiceId().getServerId());
            CloudNet.getInstance().getNetworkManager().handleServerRemove(minecraftServer);
            CloudNet.getInstance().getScreenProvider().handleDisableScreen(serverInfo.getServiceId());
        }
    }
}
