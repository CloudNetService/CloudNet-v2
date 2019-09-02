/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.network.WrapperExternal;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 30.07.2017.
 */
public class PacketOutWrapperInfo extends Packet {

    public PacketOutWrapperInfo(WrapperExternal wrapperExternal) {
        super(PacketRC.CN_CORE + 0, new Document("wrapper", wrapperExternal));
    }
}
