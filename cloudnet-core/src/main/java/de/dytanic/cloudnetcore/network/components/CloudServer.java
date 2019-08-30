/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.components;

import de.dytanic.cloudnet.lib.cloudserver.CloudServerMeta;
import de.dytanic.cloudnet.lib.server.ServerGroupType;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.service.ServiceId;
import io.netty.channel.Channel;
import lombok.EqualsAndHashCode;

/**
 * Created by Tareko on 17.10.2017.
 */
@EqualsAndHashCode
public class CloudServer implements INetworkComponent {

    private ServiceId serviceId;

    private CloudServerMeta cloudServerMeta;

    private Wrapper wrapper;

    private ServerGroupType serverGroupType;

    private ServerInfo serverInfo;

    private ServerInfo lastServerInfo;

    private Channel channel;

    public CloudServer(Wrapper wrapper, ServerInfo serverInfo, CloudServerMeta cloudServerMeta)
    {
        this.serverInfo = serverInfo;
        this.serviceId = cloudServerMeta.getServiceId();
        this.lastServerInfo = serverInfo;
        this.cloudServerMeta = cloudServerMeta;
        this.wrapper = wrapper;
        this.serverGroupType = cloudServerMeta.getServerGroupType();
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public void setLastServerInfo(ServerInfo lastServerInfo) {
        this.lastServerInfo = lastServerInfo;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    @Override
    public Channel getChannel() {
        return channel;
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

    public ServiceId getServiceId() {
        return serviceId;
    }

    @Override
    public Wrapper getWrapper() {
        return wrapper;
    }

    @Override
    public String getName()
    {
        return getServerId();
    }

    @Override
    public String getServerId()
    {
        return serviceId.getServerId();
    }

    public void disconnect()
    {
        if (this.channel != null)
        {
            this.channel.close().syncUninterruptibly();
        }
    }

}