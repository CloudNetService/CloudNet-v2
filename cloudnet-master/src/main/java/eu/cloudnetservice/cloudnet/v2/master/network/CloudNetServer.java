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

import eu.cloudnetservice.cloudnet.v2.lib.ConnectableAddress;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.logging.CloudLogger;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.api.event.network.ChannelConnectEvent;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

public final class CloudNetServer extends ChannelInitializer<Channel> {

    private final EventLoopGroup workerGroup = NetworkUtils.eventLoopGroup();
    private final EventLoopGroup bossGroup = NetworkUtils.eventLoopGroup();

    public CloudNetServer(ConnectableAddress connectableAddress) {
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                .option(ChannelOption.AUTO_READ, true)
                .channel(NetworkUtils.serverSocketChannel())
                .childOption(ChannelOption.IP_TOS, 24)
                .childOption(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.AUTO_READ, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .handler(new LoggingHandler(CloudLogger.class, LogLevel.DEBUG))
                .childHandler(this);

            CloudNet.getLogger().finest("Using " + (Epoll.isAvailable() ? "Epoll native transport" : "NIO transport"));
            CloudNet.getLogger().finest("Try to bind to " + connectableAddress.getHostName() + ':' + connectableAddress.getPort() + "...");

            serverBootstrap.bind(connectableAddress.getHostName(), connectableAddress.getPort())
                           .addListener(
                               (ChannelFutureListener) channelFuture -> {
                                   if (channelFuture.isSuccess()) {
                                       System.out.println(String.format("CloudNet is listening @%s:%d",
                                                         connectableAddress.getHostName(),
                                                         connectableAddress.getPort()));
                                       CloudNet.getInstance().getCloudServers().add(this);

                                   } else {
                                       System.out.println(String.format("Failed to bind @%s:%d",
                                                         connectableAddress.getHostName(),
                                                         connectableAddress.getPort()));
                                   }
                               })
                           .addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
                           .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                           .sync()
                           .channel()
                           .closeFuture();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    @Override
    protected void initChannel(Channel channel) {
        System.out.println("Channel [" + channel.remoteAddress() + "] connecting...");

        ChannelConnectEvent channelConnectEvent = new ChannelConnectEvent(false, channel);
        CloudNet.getInstance().getEventManager().callEvent(channelConnectEvent);
        if (channelConnectEvent.isCancelled()) {
            channel.close().syncUninterruptibly();
            return;
        }

        if (channel.remoteAddress() instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
            final String hostAddress = address.getAddress().getHostAddress();

            for (Wrapper wrapper : CloudNet.getInstance().getWrappers().values()) {
                if (wrapper.getNetworkInfo().getHostName().getHostAddress().equals(hostAddress)) {

                    NetworkUtils.initChannel(channel);
                    channel.pipeline().addLast("client", new CloudNetClientAuth(channel));
                    return;
                }
            }
        }

        channel.close().addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

    }
}
