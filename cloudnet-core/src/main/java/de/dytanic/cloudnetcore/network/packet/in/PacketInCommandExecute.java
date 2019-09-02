/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.player.PlayerCommandExecution;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

/**
 * Created by Tareko on 23.07.2017.
 */
public class PacketInCommandExecute extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        PlayerCommandExecution playerCommandExecutor = data.getObject("playerCommandExecution", PlayerCommandExecution.class);
        CloudNet.getInstance().getNetworkManager().handleCommandExecute(playerCommandExecutor);
    }
}
