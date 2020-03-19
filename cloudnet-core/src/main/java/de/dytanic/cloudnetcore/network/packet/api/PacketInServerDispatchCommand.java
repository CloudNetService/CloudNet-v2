package de.dytanic.cloudnetcore.network.packet.api;

import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;

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
