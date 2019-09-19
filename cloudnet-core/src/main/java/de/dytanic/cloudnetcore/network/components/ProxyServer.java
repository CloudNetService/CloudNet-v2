/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.components;

import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.network.NetworkInfo;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutCustomChannelMessage;
import io.netty.channel.Channel;

/**
 * Created by Tareko on 26.05.2017.
 */
public class ProxyServer implements INetworkComponent {

    private ServiceId serviceId;
    private Wrapper wrapper;
    private NetworkInfo networkInfo;

    private long channelLostTime = 0L;

    private Channel channel;
    private ProxyInfo proxyInfo;
    private ProxyInfo lastProxyInfo;
    private ProxyProcessMeta processMeta;

    public ProxyServer(ProxyProcessMeta processMeta, Wrapper wrapper, ProxyInfo proxyInfo) {
        this.processMeta = processMeta;
        this.wrapper = wrapper;
        this.serviceId = proxyInfo.getServiceId();

        this.networkInfo = new NetworkInfo(proxyInfo.getServiceId().getServerId(), proxyInfo.getHost(), proxyInfo.getPort());

        this.proxyInfo = proxyInfo;
        this.lastProxyInfo = proxyInfo;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }

    public void setProxyInfo(ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
    }

    public long getChannelLostTime() {
        return channelLostTime;
    }

    public void setChannelLostTime(long channelLostTime) {
        this.channelLostTime = channelLostTime;
    }

    public NetworkInfo getNetworkInfo() {
        return networkInfo;
    }

    public ProxyInfo getLastProxyInfo() {
        return lastProxyInfo;
    }

    public void setLastProxyInfo(ProxyInfo lastProxyInfo) {
        this.lastProxyInfo = lastProxyInfo;
    }

    public ProxyProcessMeta getProcessMeta() {
        return processMeta;
    }

    public void disconnect() {
        if (this.channel != null) {
            this.channel.close().syncUninterruptibly();
        }
    }

    public void sendCustomMessage(String channel, String message, Document value) {
        this.sendPacket(new PacketOutCustomChannelMessage(channel, message, value));
    }

    @Override
    public String getName() {
        return serviceId.getServerId();
    }

    @Override
    public Wrapper getWrapper() {
        return wrapper;
    }

    @Override
    public String getServerId() {
        return serviceId.getServerId();
    }
}
