/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ServerProcessMeta;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

public final class PacketInStartServer extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        ServerProcessMeta serverProcessMeta = data.getObject("serverProcess", new TypeToken<ServerProcessMeta>() {}.getType());

        if (!data.contains("async")) {
            System.out.println("Server process is now in queue [" + serverProcessMeta.getServiceId() + ']');
            CloudNetWrapper.getInstance().getServerProcessQueue().putProcess(serverProcessMeta);
        } else {
            CloudNetWrapper.getInstance().getServerProcessQueue().patchAsync(serverProcessMeta);
        }
    }
}
