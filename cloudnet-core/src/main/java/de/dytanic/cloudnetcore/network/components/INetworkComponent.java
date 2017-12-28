/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.components;

import de.dytanic.cloudnet.lib.network.protocol.IProtocol;
import de.dytanic.cloudnet.lib.network.protocol.ProtocolRequest;
import de.dytanic.cloudnet.lib.server.resource.ResourceMeta;
import de.dytanic.cloudnet.lib.network.ChannelUser;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnetcore.CloudNet;

/**
 * Created by Tareko on 27.05.2017.
 */
public interface INetworkComponent extends PacketSender, ChannelUser {

    String getServerId();

    Wrapper getWrapper();

    default void sendPacket(Packet packet)
    {
        CloudNet.getLogger().debug("Sending Packet " + packet.getClass().getSimpleName() + " to " + getServerId());

        if (getChannel() == null) return;
        if (getChannel().eventLoop().inEventLoop())
        {
            try
            {
                getChannel().writeAndFlush(packet);
            } catch (Exception ex)
            {
            }
        } else
        {
            getChannel().eventLoop().execute(new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        getChannel().writeAndFlush(packet);
                    } catch (Exception ex)
                    {

                    }
                }
            });
        }
    }

    default void sendPacket(Packet... packets)
    {
        for (Packet packet : packets)
        {
            sendPacket(packet);
        }
    }

    default void sendPacketSynchronized(Packet packet)
    {
        if (getChannel() == null) return;
        getChannel().writeAndFlush(packet).syncUninterruptibly();
    }

    @Override
    default void send(Object object)
    {
        if (getChannel() == null) return;

        if (getChannel().eventLoop().inEventLoop())
        {
            getChannel().writeAndFlush(object);
        } else
        {
            getChannel().eventLoop().execute(new Runnable() {
                @Override
                public void run()
                {
                    getChannel().writeAndFlush(object);
                }
            });
        }
    }

    @Override
    default void sendSynchronized(Object object)
    {
        getChannel().writeAndFlush(object).syncUninterruptibly();
    }

    @Override
    default void sendAsynchronized(Object object)
    {
        getChannel().writeAndFlush(object);
    }

    @Override
    default void send(IProtocol iProtocol, Object element)
    {
        send(new ProtocolRequest(iProtocol.getId(), element));
    }

    @Override
    default void send(int id, Object element)
    {
        send(new ProtocolRequest(id, element));
    }

    @Override
    default void sendAsynchronized(int id, Object element)
    {
        sendAsynchronized(new ProtocolRequest(id, element));
    }

    @Override
    default void sendAsynchronized(IProtocol iProtocol, Object element)
    {
        sendAsynchronized(new ProtocolRequest(iProtocol.getId(), element));
    }

    @Override
    default void sendSynchronized(int id, Object element)
    {
        sendSynchronized(new ProtocolRequest(id, element));
    }

    @Override
    default void sendSynchronized(IProtocol iProtocol, Object element)
    {
        sendSynchronized(new ProtocolRequest(iProtocol.getId(), element));
    }
}