/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.service.wrapper.WrapperScreen;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;

import java.lang.reflect.Type;

/**
 * Created by Tareko on 23.09.2017.
 */
public class PacketInWrapperScreen extends PacketInHandler {

    private static final Type TYPE = new TypeToken<WrapperScreen>() {}.getType();

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }

        CloudNet.getInstance().getNetworkManager().handleWrapperScreenInput(((Wrapper) packetSender), data.getObject("screen", TYPE));

    }
}
