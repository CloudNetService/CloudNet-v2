package eu.cloudnetservice.v2.master.network.components;

import eu.cloudnetservice.v2.lib.network.ChannelUser;
import eu.cloudnetservice.v2.lib.network.protocol.IProtocol;
import eu.cloudnetservice.v2.lib.network.protocol.ProtocolRequest;
import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.master.CloudNet;
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
        CloudNet.getLogger().finest(String.format("Sending packet %s to %s%n", packet, getServerId()));

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
        CloudNet.getLogger().finest(String.format("Sending packet %s to %s%n", packet, getServerId()));

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
    default void sendAsynchronous(Object object) {
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
    default void sendAsynchronous(int id, Object element) {
        sendAsynchronous(new ProtocolRequest(id, element));
    }

    @Override
    default void sendAsynchronous(IProtocol iProtocol, Object element) {
        sendAsynchronous(new ProtocolRequest(iProtocol.getId(), element));
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
