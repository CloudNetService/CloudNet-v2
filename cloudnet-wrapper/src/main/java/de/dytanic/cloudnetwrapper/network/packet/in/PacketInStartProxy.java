/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

public final class PacketInStartProxy extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        ProxyProcessMeta proxyProcessMeta = data.getObject("proxyProcess", new TypeToken<ProxyProcessMeta>() {}.getType());

        if (!data.contains("async")) {
            System.out.println("Proxy process is now in queue [" + proxyProcessMeta.getServiceId() + ']');
            CloudNetWrapper.getInstance().getServerProcessQueue().putProcess(proxyProcessMeta);
        } else {
            CloudNetWrapper.getInstance().getServerProcessQueue().patchAsync(proxyProcessMeta);
        }
    }
}
