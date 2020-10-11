/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.master.network.components;

import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyProcessMeta;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.master.network.NetworkInfo;
import eu.cloudnetservice.cloudnet.v2.master.network.packet.out.PacketOutCustomChannelMessage;
import io.netty.channel.Channel;

public class ProxyServer implements INetworkComponent {

    private final ServiceId serviceId;
    private final Wrapper wrapper;
    private final NetworkInfo networkInfo;

    private long channelLostTime = 0L;

    private Channel channel;
    private ProxyInfo proxyInfo;
    private ProxyInfo lastProxyInfo;
    private final ProxyProcessMeta processMeta;

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
