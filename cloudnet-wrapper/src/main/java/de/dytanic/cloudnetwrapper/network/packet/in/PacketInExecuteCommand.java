/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

public class PacketInExecuteCommand extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        String commandLine = data.getString("commandLine");
        CloudNetWrapper.getInstance().getCommandManager().dispatchCommand(commandLine);
    }
}
