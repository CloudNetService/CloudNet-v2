package eu.cloudnetservice.v2.master.network.packet.api;

import eu.cloudnetservice.v2.lib.DefaultType;
import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.v2.master.network.components.ProxyServer;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInServerDispatchCommand implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        DefaultType defaultType = packet.getData().getObject("defaultType", DefaultType.TYPE);
        String serverId = packet.getData().getString("serverId");
        String commandLine = packet.getData().getString("commandLine");

        if (defaultType == DefaultType.BUKKIT) {
            MinecraftServer minecraftServer = CloudNet.getInstance().getServer(serverId);
            if (minecraftServer != null) {
                minecraftServer.getWrapper().writeServerCommand(commandLine, minecraftServer.getServerInfo());
            }
        } else if (defaultType == DefaultType.BUNGEE_CORD) {
            ProxyServer proxyServer = CloudNet.getInstance().getProxy(serverId);
            if (proxyServer != null) {
                proxyServer.getWrapper().writeProxyCommand(commandLine, proxyServer.getProxyInfo());
            }
        }
    }
}
