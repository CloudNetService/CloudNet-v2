package eu.cloudnetservice.v2.master.network.packet.in;

import eu.cloudnetservice.v2.lib.DefaultType;
import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.lib.utility.document.Document;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.v2.master.network.components.ProxyServer;
import eu.cloudnetservice.v2.master.network.packet.out.PacketOutCustomSubChannelMessage;

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
