package de.dytanic.cloudnetwrapper.network.packet.in;

import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.server.BungeeCord;
import de.dytanic.cloudnetwrapper.server.GameServer;

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
