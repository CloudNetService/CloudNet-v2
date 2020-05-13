package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutCustomSubChannelMessage;

/**
 * Created by Tareko on 24.08.2017.
 */
public class PacketInCustomSubChannelMessage implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        DefaultType defaultType = packet.getData().getObject("defaultType", DefaultType.class);
        String channel = packet.getData().getString("channel");
        String message = packet.getData().getString("message");
        Document document = packet.getData().getDocument("value");
        if (defaultType.equals(DefaultType.BUKKIT)) {
            if (packet.getData().contains("serverId")) {
                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(packet.getData().getString("serverId"));
                if (minecraftServer != null) {
                    minecraftServer.sendPacket(new PacketOutCustomSubChannelMessage(channel, message, document));
                }
            } else {
                CloudNet.getInstance().getNetworkManager().sendAll(new PacketOutCustomSubChannelMessage(channel, message, document),
                                                                   MinecraftServer.class::isInstance);
            }
        } else {
            if (packet.getData().contains("serverId")) {
                ProxyServer proxyServer = CloudNet.getInstance().getProxy(packet.getData().getString("serverId"));
                if (proxyServer != null) {
                    proxyServer.sendPacket(new PacketOutCustomSubChannelMessage(channel, message, document));
                }
            } else {
                CloudNet.getInstance().getNetworkManager().sendToProxy(new PacketOutCustomSubChannelMessage(channel, message, document));
            }
        }
    }
}
