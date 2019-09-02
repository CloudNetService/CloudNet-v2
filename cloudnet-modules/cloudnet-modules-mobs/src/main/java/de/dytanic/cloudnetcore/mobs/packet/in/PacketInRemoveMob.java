/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.mobs.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.mobs.MobModule;

/**
 * Created by Tareko on 01.09.2017.
 */
public class PacketInRemoveMob extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        ServerMob serverMob = data.getObject("mob", new TypeToken<ServerMob>() {}.getType());
        MobModule.getInstance().getMobDatabase().remove(serverMob);
        CloudNet.getInstance().getNetworkManager().reload();
        CloudNet.getInstance().getNetworkManager().updateAll();
    }
}
