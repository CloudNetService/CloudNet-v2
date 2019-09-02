/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.components;

import de.dytanic.cloudnet.lib.cloudserver.CloudServerMeta;
import de.dytanic.cloudnet.lib.server.ServerGroupType;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.service.ServiceId;
import io.netty.channel.Channel;

import java.util.Objects;

/**
 * Created by Tareko on 17.10.2017.
 */
public class CloudServer implements INetworkComponent {

    private ServiceId serviceId;

    private CloudServerMeta cloudServerMeta;

    private Wrapper wrapper;

    private ServerGroupType serverGroupType;

    private ServerInfo serverInfo;

    private ServerInfo lastServerInfo;

    private Channel channel;

    public CloudServer(Wrapper wrapper, ServerInfo serverInfo, CloudServerMeta cloudServerMeta) {
        this.serverInfo = serverInfo;
        this.serviceId = cloudServerMeta.getServiceId();
        this.lastServerInfo = serverInfo;
        this.cloudServerMeta = cloudServerMeta;
        this.wrapper = wrapper;
        this.serverGroupType = cloudServerMeta.getServerGroupType();
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public CloudServerMeta getCloudServerMeta() {
        return cloudServerMeta;
    }

    public ServerGroupType getServerGroupType() {
        return serverGroupType;
    }

    public ServerInfo getLastServerInfo() {
        return lastServerInfo;
    }

    public void setLastServerInfo(ServerInfo lastServerInfo) {
        this.lastServerInfo = lastServerInfo;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    @Override
    public String getName() {
        return getServerId();
    }

    @Override
    public Wrapper getWrapper() {
        return wrapper;
    }

    @Override
    public String getServerId() {
        return serviceId.getServerId();
    }

    public void disconnect() {
        if (this.channel != null) {
            this.channel.close().syncUninterruptibly();
        }
    }

    @Override
    public int hashCode() {
        int result = serviceId != null ? serviceId.hashCode() : 0;
        result = 31 * result + (cloudServerMeta != null ? cloudServerMeta.hashCode() : 0);
        result = 31 * result + (wrapper != null ? wrapper.hashCode() : 0);
        result = 31 * result + (serverGroupType != null ? serverGroupType.hashCode() : 0);
        result = 31 * result + (serverInfo != null ? serverInfo.hashCode() : 0);
        result = 31 * result + (lastServerInfo != null ? lastServerInfo.hashCode() : 0);
        result = 31 * result + (channel != null ? channel.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CloudServer)) {
            return false;
        }
        final CloudServer that = (CloudServer) o;
        return Objects.equals(serviceId, that.serviceId) && Objects.equals(cloudServerMeta, that.cloudServerMeta) && Objects.equals(wrapper,
                                                                                                                                    that.wrapper) && serverGroupType == that.serverGroupType && Objects
            .equals(serverInfo, that.serverInfo) && Objects.equals(lastServerInfo, that.lastServerInfo) && Objects.equals(channel,
                                                                                                                          that.channel);
    }
}
