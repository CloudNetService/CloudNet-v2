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

package eu.cloudnetservice.cloudnet.v2.lib.network;

import eu.cloudnetservice.cloudnet.v2.lib.ConnectableAddress;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.network.auth.Auth;
import eu.cloudnetservice.cloudnet.v2.lib.network.auth.packetio.PacketOutAuth;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.IProtocol;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.ProtocolRequest;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketManager;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by Tareko on 22.07.2017.
 */
public final class NetworkConnection implements PacketSender {

    private final PacketManager packetManager = new PacketManager();
    private final EventLoopGroup eventLoopGroup = NetworkUtils.eventLoopGroup(2);
    private final ConnectableAddress localAddress;
    private final ConnectableAddress connectableAddress;
    private Channel channel;
    private long connectionTries = 0;

    private final Logger logger = Logger.getLogger("CloudLogger");

    public NetworkConnection(ConnectableAddress connectableAddress, final ConnectableAddress localAddress) {
        this.connectableAddress = connectableAddress;
        this.localAddress = localAddress;
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

    public Channel getChannel() {
        return channel;
    }

    protected void setChannel(Channel channel) {
        this.channel = channel;
    }

    public ConnectableAddress getConnectableAddress() {
        return connectableAddress;
    }

    public long getConnectionTries() {
        return connectionTries;
    }

    @Override
    public String getName() {
        return "Network-Connector";
    }

    @Override
    public void sendPacket(Packet... packets) {

        if (channel == null) {
            return;
        }

        if (channel.eventLoop().inEventLoop()) {
            for (Packet packet : packets) {
                channel.writeAndFlush(packet);
            }
        } else {
            channel.eventLoop().execute(() -> {
                for (Packet packet : packets) {
                    channel.writeAndFlush(packet);
                }
            });
        }
    }

    public boolean tryDisconnect() {
        if (channel != null) {
            channel.close().syncUninterruptibly().addListener(ChannelFutureListener.CLOSE);
        }
        try {
            eventLoopGroup.shutdownGracefully().await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }



        return false;
    }

    @Override
    public void sendPacket(Packet packet) {
        if (channel == null) {
            return;
        }

        if (channel.eventLoop().inEventLoop()) {
            channel.writeAndFlush(packet);
        } else {
            channel.eventLoop().execute(() -> channel.writeAndFlush(packet));
        }
    }

    @Override
    public void sendPacketSynchronized(Packet packet) {
        if (channel == null) {
            return;
        }

        if (channel.eventLoop().inEventLoop()) {
            channel.writeAndFlush(packet).syncUninterruptibly();
        } else {
            try {
                channel.eventLoop().invokeAll(Collections.singletonList(() -> channel.writeAndFlush(packet).syncUninterruptibly()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }

    public void tryConnect(final NetDispatcher netDispatcher, final Auth auth) {
        tryConnect(netDispatcher, auth, null);
    }

    public boolean tryConnect(SimpleChannelInboundHandler<Packet> channelInboundHandler, Auth auth, Runnable cancelTask) {
        try {
            Bootstrap bootstrap = new Bootstrap().option(ChannelOption.AUTO_READ, true)
                                                 .group(eventLoopGroup)
                                                 .handler(new ChannelInitializer<Channel>() {
                                                     @Override
                                                     protected void initChannel(Channel channel) {
                                                         NetworkUtils.initChannel(channel).pipeline().addLast(channelInboundHandler);
                                                     }
                                                 })
                                                 .channel(NetworkUtils.socketChannel());
            InetAddress addr = connectableAddress.getHostName();
            InetSocketAddress dest = new InetSocketAddress(addr, connectableAddress.getPort());
            InetSocketAddress localAddress = new InetSocketAddress(this.localAddress.getHostName(), this.localAddress.getPort());

            this.channel = bootstrap.connect(dest, localAddress)
                                    .sync()
                                    .channel()
                                    .writeAndFlush(new PacketOutAuth(auth))
                                    .syncUninterruptibly()
                                    .channel();

            return true;
        } catch (Exception ex) {
            connectionTries++;
            logger.info(String.format("Failed to connect... [%d] (%s)", connectionTries, ex.getMessage()));
            //            ex.printStackTrace();

            if (this.channel != null) {
                this.channel.close().syncUninterruptibly().addListener(ChannelFutureListener.CLOSE);
                this.channel = null;
            }

            if (cancelTask != null) {
                cancelTask.run();
            }

            return false;
        }
    }

    public boolean isConnected() {
        return channel != null;
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
        NetworkUtils.getExecutor().submit(() -> {
            channel.writeAndFlush(object);
        });
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
