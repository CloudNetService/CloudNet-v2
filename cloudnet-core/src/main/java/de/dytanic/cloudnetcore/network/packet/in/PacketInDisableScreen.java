/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

/**
 * Created by Tareko on 25.08.2017.
 */
public class PacketInDisableScreen extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        ServiceId serviceId = data.getObject("serviceId", new TypeToken<ServiceId>() {}.getType());
        CloudNet.getInstance().getScreenProvider().handleDisableScreen(serviceId);
    }
}
