/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol.packet;

import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.UUID;

/**
 * Created by Tareko on 18.07.2017.
 */
public abstract class PacketInHandler {

    protected UUID packetUniqueId;

    public PacketInHandler() {
    }

    public abstract void handleInput(Document data, PacketSender packetSender);
}
