/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import java.util.Collection;
import java.util.function.Function;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketAPIInGetProxys extends PacketAPIIO {

    @Override
    public void handleInput(Document data, PacketSender packetSender)
    {
        if (data.contains("group"))
        {
            Collection<ProxyInfo> proxyInfos = CollectionWrapper.transform(CloudNet.getInstance().getProxys(data.getString("group")), new Function<ProxyServer, ProxyInfo>() {
                @Override
                public ProxyInfo apply(ProxyServer key)
                {
                    return key.getProxyInfo();
                }
            });
            packetSender.sendPacket(getResult(new Document("proxyInfos", proxyInfos)));
        } else
        {
            Collection<ProxyInfo> proxyInfos = CollectionWrapper.transform(CloudNet.getInstance().getProxys().values(), new Function<ProxyServer, ProxyInfo>() {
                @Override
                public ProxyInfo apply(ProxyServer key)
                {
                    return key.getProxyInfo();
                }
            });
            packetSender.sendPacket(getResult(new Document("proxyInfos", proxyInfos)));
        }
    }

    @Override
    protected Packet getResult(Document value)
    {
        return new Packet(packetUniqueId, PacketRC.SERVER_HANDLE, value);
    }
}