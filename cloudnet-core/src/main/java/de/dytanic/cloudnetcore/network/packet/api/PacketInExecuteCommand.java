/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api;

import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInExecuteCommand extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        String commandLine = data.getString("command");
        if (commandLine != null) {
            CloudNet.getInstance().getCommandManager().dispatchCommand(commandLine);
        }
    }
}
