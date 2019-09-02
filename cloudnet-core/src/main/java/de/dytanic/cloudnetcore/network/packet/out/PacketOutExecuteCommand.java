/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 30.07.2017.
 */
public class PacketOutExecuteCommand extends Packet {
    public PacketOutExecuteCommand(String command) {
        super(PacketRC.CN_CORE + 9, new Document("commandLine", command));
    }
}
