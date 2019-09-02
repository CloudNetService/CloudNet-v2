/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

public class PacketInStopProxy extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        ProxyInfo serverInfo = data.getObject("proxyInfo", new TypeToken<ProxyInfo>() {}.getType());
        if (CloudNetWrapper.getInstance().getProxys().containsKey(serverInfo.getServiceId().getServerId())) {
            CloudNetWrapper.getInstance().getProxys().get(serverInfo.getServiceId().getServerId()).shutdown();
        }
    }
}
