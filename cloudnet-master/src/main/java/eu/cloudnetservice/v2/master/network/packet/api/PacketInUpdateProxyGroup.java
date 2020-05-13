package eu.cloudnetservice.v2.master.network.packet.api;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.command.CommandReload;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInUpdateProxyGroup implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        ProxyGroup proxyGroup = packet.getData().getObject("group", ProxyGroup.TYPE);
        CloudNet.getInstance().getConfig().createGroup(proxyGroup);

        CommandReload.reloadConfig();
    }
}
