package eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.in;

import eu.cloudnetservice.cloudnet.v2.lib.DefaultType;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;

public class PacketInExecuteServerCommand implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (packet.getData().getObject("type", DefaultType.class).equals(DefaultType.BUKKIT)) {
            ServerInfo serverInfo = packet.getData().getObject("serverInfo", ServerInfo.TYPE);
            if (CloudNetWrapper.getInstance().getServers().containsKey(serverInfo.getServiceId().getServerId())) {
                CloudNetWrapper.getInstance().getServers().get(
                    serverInfo.getServiceId().getServerId()).executeCommand(
                    packet.getData().getString("commandLine"));
            }
        } else {
            ProxyInfo serverInfo = packet.getData().getObject("proxyInfo", ProxyInfo.TYPE);
            if (CloudNetWrapper.getInstance().getProxies().containsKey(serverInfo.getServiceId().getServerId())) {
                CloudNetWrapper.getInstance().getProxies().get(
                    serverInfo.getServiceId().getServerId()).executeCommand(
                    packet.getData().getString("commandLine"));
            }
        }
    }
}
