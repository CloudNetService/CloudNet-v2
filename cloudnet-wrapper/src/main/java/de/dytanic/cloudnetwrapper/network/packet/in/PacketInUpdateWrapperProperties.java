/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import net.md_5.bungee.config.Configuration;

/**
 * Created by Tareko on 30.09.2017.
 */
public class PacketInUpdateWrapperProperties extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        Configuration configuration = data.getObject("configuration", new TypeToken<Configuration>() {}.getType());
        CloudNetWrapper.getInstance().getWrapperConfig().getConfiguration().self.putAll(configuration.self);
        CloudNetWrapper.getInstance().getWrapperConfig().save();
        CloudNetWrapper.getInstance().getWrapperConfig().load();
    }
}
