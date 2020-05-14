package eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.in;

import eu.cloudnetservice.cloudnet.v2.lib.DefaultType;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;
import eu.cloudnetservice.cloudnet.v2.wrapper.server.BungeeCord;
import eu.cloudnetservice.cloudnet.v2.wrapper.server.GameServer;

public final class PacketInScreen implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (packet.getData().getObject("type", DefaultType.class) != DefaultType.BUNGEE_CORD) {
            ServerInfo server = packet.getData().getObject("serverInfo", ServerInfo.TYPE);
            if (CloudNetWrapper.getInstance().getServers().containsKey(server.getServiceId().getServerId())) {
                GameServer gameServer = CloudNetWrapper.getInstance().getServers().get(server.getServiceId().getServerId());

                if (packet.getData().getBoolean("enable")) {
                    gameServer.enableScreenSystem();
                } else {
                    gameServer.disableScreenSystem();
                }
            }
        } else {
            ProxyInfo server = packet.getData().getObject("proxyInfo", ProxyInfo.TYPE);
            if (CloudNetWrapper.getInstance().getProxies().containsKey(server.getServiceId().getServerId())) {
                BungeeCord bungee = CloudNetWrapper.getInstance().getProxies().get(server.getServiceId().getServerId());
                if (packet.getData().getBoolean("enable")) {
                    bungee.enableScreenSystem();
                } else {
                    bungee.disableScreenSystem();
                }
            }
        }
    }
}
