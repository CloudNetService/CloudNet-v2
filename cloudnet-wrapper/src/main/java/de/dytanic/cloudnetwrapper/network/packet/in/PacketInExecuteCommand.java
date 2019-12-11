/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

public class PacketInExecuteCommand implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        String commandLine = packet.getData().getString("commandLine");
        CloudNetWrapper.getInstance().getCommandManager().dispatchCommand(commandLine);
    }
}
