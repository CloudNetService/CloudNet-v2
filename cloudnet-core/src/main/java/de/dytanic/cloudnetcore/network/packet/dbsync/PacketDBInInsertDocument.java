/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.dbsync;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

/**
 * Created by Tareko on 25.08.2017.
 */
public final class PacketDBInInsertDocument extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        CloudNet.getInstance().getDatabaseManager().getDatabase(data.getString("db")).insert(data.getObject("insert",
                                                                                                            new TypeToken<Document[]>() {}.getType()));
    }
}
