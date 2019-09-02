/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.network.components.Wrapper;

public class PacketInSetReadyWrapper extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }

        boolean ready = data.getBoolean("ready");
        ((Wrapper) packetSender).setReady(ready);
    }
}
