/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.signs.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.signs.SignsModule;

/**
 * Created by Tareko on 22.08.2017.
 */
public class PacketInAddSign extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        Sign sign = data.getObject("sign", new TypeToken<Sign>() {}.getType());
        SignsModule.getInstance().getSignDatabase().appendSign(sign);

        CloudNet.getInstance().getNetworkManager().reload();
        CloudNet.getInstance().getNetworkManager().updateAll();
    }
}
