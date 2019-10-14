/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.components;

import de.dytanic.cloudnet.lib.network.ChannelUser;
import de.dytanic.cloudnet.lib.network.protocol.IProtocol;
import de.dytanic.cloudnet.lib.network.protocol.ProtocolRequest;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnetcore.CloudNet;
import io.netty.channel.ChannelFutureListener;

/**
 * Created by Tareko on 27.05.2017.
 */
public interface INetworkComponent extends PacketSender, ChannelUser {

    Wrapper getWrapper();

    default void sendPacket(Packet... packets) {
        for (Packet packet : packets) {
            sendPacket(packet);
        }
    }

    String getServerId();

    default void sendPacket(Packet packet) {
        CloudNet.getLogger().debug("Sending Packet " + packet.getClass().getSimpleName() + " (id=" + CloudNet.getInstance()
                                                                                                             .getPacketManager()
                                                                                                             .packetId(packet) + ";dataLength=" + CloudNet
            .getInstance()
            .getPacketManager()
            .packetData(packet)
            .size() + ") to " + getServerId());

        if (getChannel() == null) {
            return;
        }
        if (getChannel().eventLoop().inEventLoop()) {
            try {
                getChannel().writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            } catch (Exception ignored) {
            }
        } else {
            getChannel().eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        getChannel().writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                    } catch (Exception ignored) {
                    }
                }
            });
        }
    }

    default void sendPacketSynchronized(Packet packet) {
        if (getChannel() == null) {
            return;
        }
        CloudNet.getLogger().debug("Sending Packet " + packet.getClass().getSimpleName() + " (id=" + CloudNet.getInstance()
                                                                                                             .getPacketManager()
                                                                                                             .packetId(packet) + ";dataLength=" + CloudNet
            .getInstance()
            .getPacketManager()
            .packetData(packet)
            .size() + ") to " + getServerId());
        getChannel().writeAndFlush(packet).syncUninterruptibly();
    }

    @Override
    default void send(Object object) {
        if (getChannel() == null) {
            return;
        }

        if (getChannel().eventLoop().inEventLoop()) {
            getChannel().writeAndFlush(object);
        } else {
            getChannel().eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    getChannel().writeAndFlush(object);
                }
            });
        }
    }

    @Override
    default void sendSynchronized(Object object) {
        getChannel().writeAndFlush(object).syncUninterruptibly();
    }

    @Override
    default void sendAsynchronized(Object object) {
        getChannel().writeAndFlush(object);
    }

    @Override
    default void send(IProtocol iProtocol, Object element) {
        send(new ProtocolRequest(iProtocol.getId(), element));
    }

    @Override
    default void send(int id, Object element) {
        send(new ProtocolRequest(id, element));
    }

    @Override
    default void sendAsynchronized(int id, Object element) {
        sendAsynchronized(new ProtocolRequest(id, element));
    }

    @Override
    default void sendAsynchronized(IProtocol iProtocol, Object element) {
        sendAsynchronized(new ProtocolRequest(iProtocol.getId(), element));
    }

    @Override
    default void sendSynchronized(int id, Object element) {
        sendSynchronized(new ProtocolRequest(id, element));
    }

    @Override
    default void sendSynchronized(IProtocol iProtocol, Object element) {
        sendSynchronized(new ProtocolRequest(iProtocol.getId(), element));
    }
}
