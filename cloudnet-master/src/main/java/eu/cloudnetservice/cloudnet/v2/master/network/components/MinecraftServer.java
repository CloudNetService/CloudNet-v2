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

import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroupMode;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerProcessMeta;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.packet.out.PacketOutCustomSubChannelMessage;
import io.netty.channel.Channel;

public final class MinecraftServer implements INetworkComponent {

    private final ServiceId serviceId;
    private final ServerProcessMeta processMeta;
    private final Wrapper wrapper;
    private final ServerGroupMode groupMode;

    private long channelLostTime = 0L;

    private ServerInfo serverInfo;
    private ServerInfo lastServerInfo;
    private Channel channel;

    public MinecraftServer(ServerProcessMeta processMeta, Wrapper wrapper, ServerGroup group, ServerInfo serverInfo) {
        this.processMeta = processMeta;
        this.serviceId = serverInfo.getServiceId();
        this.wrapper = wrapper;
        this.groupMode = group.getGroupMode();

        this.serverInfo = serverInfo;
        this.lastServerInfo = serverInfo;
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

    public long getChannelLostTime() {
        return channelLostTime;
    }

    public void setChannelLostTime(long channelLostTime) {
        this.channelLostTime = channelLostTime;
    }

    public ServerInfo getLastServerInfo() {
        return lastServerInfo;
    }

    public void setLastServerInfo(ServerInfo lastServerInfo) {
        this.lastServerInfo = lastServerInfo;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public ServerGroupMode getGroupMode() {
        return groupMode;
    }

    public ServerProcessMeta getProcessMeta() {
        return processMeta;
    }

    public void disconnect() {
        if (this.channel != null) {
            this.channel.close().syncUninterruptibly();
        }
    }

    public void sendCustomMessage(String channel, String message, Document value) {
        this.sendPacket(new PacketOutCustomSubChannelMessage(channel, message, value));
    }

    public ServerGroup getGroup() {
        return CloudNet.getInstance().getServerGroup(serviceId.getGroup());
    }

    @Override
    public Wrapper getWrapper() {
        return wrapper;
    }

    @Override
    public String getServerId() {
        return serviceId.getServerId();
    }

    @Override
    public String getName() {
        return serviceId.getServerId();
    }
}
