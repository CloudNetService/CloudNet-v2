package de.dytanic.cloudnetcore.network.packet.api;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.command.CommandReload;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInUpdateServerGroup implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        ServerGroup proxyGroup = packet.getData().getObject("group", ServerGroup.TYPE);
        CloudNet.getInstance().getConfig().createGroup(proxyGroup);

        CommandReload.reloadConfig();
    }
}
