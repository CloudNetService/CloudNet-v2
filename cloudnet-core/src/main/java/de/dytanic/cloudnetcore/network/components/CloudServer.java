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
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Tareko on 17.10.2017.
 */
@EqualsAndHashCode
@Getter
public class CloudServer implements INetworkComponent {

    private ServiceId serviceId;

    private CloudServerMeta cloudServerMeta;

    private Wrapper wrapper;

    private ServerGroupType serverGroupType;

    @Setter
    private ServerInfo serverInfo;

    @Setter
    private ServerInfo lastServerInfo;

    @Setter
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