/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.components;

import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.resource.ResourceMeta;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.network.NetworkInfo;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutCustomChannelMessage;
import de.dytanic.cloudnetcore.util.defaults.DefaultResourceMeta;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Tareko on 26.05.2017.
 */
@Getter
public class ProxyServer
        implements INetworkComponent {

    private ServiceId serviceId;
    private Wrapper wrapper;
    private NetworkInfo networkInfo;

    @Setter
    private long channelLostTime = 0L;

    @Setter
    private Channel channel;
    @Setter
    private ProxyInfo proxyInfo;
    @Setter
    private ProxyInfo lastProxyInfo;
    private ProxyProcessMeta processMeta;

    public ProxyServer(
            ProxyProcessMeta processMeta,
            Wrapper wrapper,
            ProxyInfo proxyInfo
    )
    {
        this.processMeta = processMeta;
        this.wrapper = wrapper;
        this.serviceId = proxyInfo.getServiceId();

        this.networkInfo = new NetworkInfo(proxyInfo.getServiceId().getServerId(), proxyInfo.getHost(), proxyInfo.getPort());

        this.proxyInfo = proxyInfo;
        this.lastProxyInfo = proxyInfo;
    }

    public void disconnect()
    {
        if (this.channel != null)
        {
            this.channel.close().syncUninterruptibly();
        }
    }

    public void sendCustomMessage(String channel, String message, Document value)
    {
        this.sendPacket(new PacketOutCustomChannelMessage(channel, message, value));
    }

    @Override
    public String getName()
    {
        return serviceId.getServerId();
    }

    @Override
    public String getServerId()
    {
        return serviceId.getServerId();
    }
}