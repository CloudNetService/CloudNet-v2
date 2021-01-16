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

package eu.cloudnetservice.cloudnet.v2.master.network;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.IProtocol;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.ProtocolRequest;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class CloudNetClientAuth extends SimpleChannelInboundHandler<Packet> implements PacketSender {

    private final Channel channel;

    public CloudNetClientAuth(Channel channel) {
        this.channel = channel;
    }

    @Override
    public String getName() {
        return "Unknown-Connection";
    }

    @Override
    public void sendPacket(Packet... packets) {
        if (channel != null) {
            for (Packet packet : packets) {
                sendPacket(packet);
            }
        }
    }

    @Override
    public void sendPacket(Packet packet) {
        if (channel != null) {
            if (channel.eventLoop().inEventLoop()) {
                channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            } else {
                channel.eventLoop().execute(() -> channel.writeAndFlush(packet)
                                                         .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE));
            }
        }
    }

    @Override
    public void sendPacketSynchronized(Packet packet) {
        channel.writeAndFlush(packet).syncUninterruptibly();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if ((!channel.isActive() || !channel.isOpen() || !channel.isWritable())) {
            channel.close().syncUninterruptibly();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        CloudNet.getLogger().finest(String.format("Receiving packet %s from %s%n", packet, channel.remoteAddress()));
        if (packet.getId() == (PacketRC.INTERNAL - 1)) {
            CloudNet.getInstance().getPacketManager().dispatchPacket(packet, this);
        }
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public void send(Object object) {
        if (channel == null) {
            return;
        }

        if (channel.eventLoop().inEventLoop()) {
            channel.writeAndFlush(object);
        } else {
            channel.eventLoop().execute(() -> channel.writeAndFlush(object));
        }
    }

    @Override
    public void sendSynchronized(Object object) {
        channel.writeAndFlush(object).syncUninterruptibly();
    }

    @Override
    public void sendAsynchronous(Object object) {
        channel.writeAndFlush(object);
    }

    @Override
    public void send(IProtocol iProtocol, Object element) {
        send(new ProtocolRequest(iProtocol.getId(), element));
    }

    @Override
    public void send(int id, Object element) {
        send(new ProtocolRequest(id, element));
    }

    @Override
    public void sendAsynchronous(int id, Object element) {
        sendAsynchronous(new ProtocolRequest(id, element));
    }

    @Override
    public void sendAsynchronous(IProtocol iProtocol, Object element) {
        sendAsynchronous(new ProtocolRequest(iProtocol.getId(), element));
    }

    @Override
    public void sendSynchronized(int id, Object element) {
        sendSynchronized(new ProtocolRequest(id, element));
    }

    @Override
    public void sendSynchronized(IProtocol iProtocol, Object element) {
        sendSynchronized(new ProtocolRequest(iProtocol.getId(), element));
    }

}
