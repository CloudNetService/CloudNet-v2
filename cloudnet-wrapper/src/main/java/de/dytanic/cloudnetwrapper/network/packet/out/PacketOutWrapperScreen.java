/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.service.wrapper.WrapperScreen;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

/**
 * Created by Tareko on 23.09.2017.
 */
public class PacketOutWrapperScreen extends Packet {

    public PacketOutWrapperScreen(String line) {
        super(PacketRC.CN_WRAPPER + 12,
              new Document("screen", new WrapperScreen(CloudNetWrapper.getInstance().getWrapperConfig().getWrapperId(), line)));
    }
}
