/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.handlers.NetworkHandler;
import de.dytanic.cloudnet.api.network.packet.PacketInHandlerDefault;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;

/**
 * Created by Tareko on 17.08.2017.
 */
public final class PacketInProxyRemove extends PacketInHandlerDefault {
    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        ProxyInfo proxyInfo = data.getObject("proxyInfo", new TypeToken<ProxyInfo>() {}.getType());
        if (CloudAPI.getInstance() != null) {
            CloudAPI.getInstance().getNetworkHandlerProvider().iterator(new Runnabled<NetworkHandler>() {
                @Override
                public void run(NetworkHandler obj) {
                    obj.onProxyRemove(proxyInfo);
                }
            });
        }
    }
}
