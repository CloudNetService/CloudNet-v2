/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.cloudserver.CloudServerMeta;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

/**
 * Created by Tareko on 22.10.2017.
 */
public class PacketInStartCloudServer extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        CloudServerMeta cloudServerMeta = data.getObject("cloudServerMeta", new TypeToken<CloudServerMeta>() {}.getType());

        if (!data.contains("async")) {
            System.out.println("Cloud game server process is now in queue [" + cloudServerMeta.getServiceId() + ']');
            CloudNetWrapper.getInstance().getServerProcessQueue().putProcess(cloudServerMeta);
        } else {
            CloudNetWrapper.getInstance().getServerProcessQueue().patchAsync(cloudServerMeta);
        }
    }
}
