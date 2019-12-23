package de.dytanic.cloudnetcore.network;

import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.api.event.network.ChannelConnectEvent;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import joptsimple.OptionSet;

import java.net.InetSocketAddress;

/**
 * Created by Tareko on 26.05.2017.
 */
public final class CloudNetServer extends ChannelInitializer<Channel> implements AutoCloseable {

    private SslContext sslContext;
    private final EventLoopGroup workerGroup = NetworkUtils.eventLoopGroup();
    private final EventLoopGroup bossGroup = NetworkUtils.eventLoopGroup();

    public CloudNetServer(OptionSet optionSet, ConnectableAddress connectableAddress) {
        try {
            if (optionSet.has("ssl")) {
                CloudNet.getLogger().finest("Enabling SSL Context for service requests");
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslContext = SslContextBuilder
                    .forServer(ssc.certificate(), ssc.privateKey())
                    .build();
            }

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
                .childHandler(this);

            CloudNet.getLogger().finest("Using " + (Epoll.isAvailable() ? "Epoll native transport" : "NIO transport"));
            CloudNet.getLogger().finest("Try to bind to " + connectableAddress.getHostName() + ':' + connectableAddress.getPort() + "...");

            serverBootstrap.bind(connectableAddress.getHostName(), connectableAddress.getPort())
                           .addListener(
                               (ChannelFutureListener) channelFuture -> {
                                   if (channelFuture.isSuccess()) {
                                       System.out.printf("CloudNet is listening @%s:%d%n",
                                                         connectableAddress.getHostName(),
                                                         connectableAddress.getPort());
                                       CloudNet.getInstance().getCloudServers().add(this);

                                   } else {
                                       System.out.printf("Failed to bind @%s:%d%n",
                                                         connectableAddress.getHostName(),
                                                         connectableAddress.getPort());
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

    @Override
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

            for (Wrapper wrapper : CloudNet.getInstance().getWrappers().values()) {
                if (wrapper.getNetworkInfo().getHostName().equalsIgnoreCase(address.getAddress().getHostAddress())) {

                    if (sslContext != null) {
                        channel.pipeline().addLast(sslContext.newHandler(channel.alloc()));
                    }

                    NetworkUtils.initChannel(channel);
                    channel.pipeline().addLast("client", new CloudNetClientAuth(channel));
                    return;
                }
            }
        }

        channel.close().addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

    }
}
