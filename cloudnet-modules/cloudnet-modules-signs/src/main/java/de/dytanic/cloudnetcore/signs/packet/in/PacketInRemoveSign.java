/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.signs.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.signs.SignsModule;

/**
 * Created by Tareko on 23.08.2017.
 */
public class PacketInRemoveSign implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        Sign sign = packet.getData().getObject("sign", Sign.TYPE);
        SignsModule.getInstance().getSignDatabase().removeSign(sign.getUniqueId());

        CloudNet.getInstance().getNetworkManager().reload();
        CloudNet.getInstance().getNetworkManager().updateAll();
    }
}
