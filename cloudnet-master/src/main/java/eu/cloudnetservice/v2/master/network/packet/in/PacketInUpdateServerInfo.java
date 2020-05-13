package eu.cloudnetservice.v2.master.network.packet.in;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.components.MinecraftServer;

/**
 * Created by Tareko on 23.07.2017.
 */
public class PacketInUpdateServerInfo implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (packetSender instanceof MinecraftServer) {
            MinecraftServer minecraftServer = (MinecraftServer) packetSender;
            minecraftServer.setLastServerInfo(minecraftServer.getServerInfo());
            minecraftServer.setServerInfo(packet.getData().getObject("serverInfo", ServerInfo.TYPE));
            CloudNet.getInstance().getNetworkManager().handleServerInfoUpdate(
                minecraftServer, packet.getData().getObject("serverInfo", ServerInfo.TYPE));
        }
    }
}
