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
import io.netty.handler.ssl.util.SelfSignedCertificate;
import joptsimple.OptionSet;

/**
 * Created by Tareko on 26.05.2017.
 */
public final class CloudNetServer extends ChannelInitializer<Channel> implements AutoCloseable {

    private SslContext sslContext;
    private EventLoopGroup workerGroup = NetworkUtils.eventLoopGroup(), bossGroup = NetworkUtils.eventLoopGroup();

    public CloudNetServer(OptionSet optionSet, ConnectableAddress connectableAddress) {
        try {
            if (optionSet.has("ssl")) {
                CloudNet.getLogger().debug("Enabling SSL Context for service requests");
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslContext = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
            }

            ServerBootstrap serverBootstrap = new ServerBootstrap().group(bossGroup, workerGroup)

                                                                   .option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                                                                   .option(ChannelOption.AUTO_READ,
                                                                           true)

                                                                   .channel(NetworkUtils.serverSocketChannel())

                                                                   .childOption(ChannelOption.IP_TOS, 24)
                                                                   .childOption(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                                                                   .childOption(ChannelOption.TCP_NODELAY, true)
                                                                   .childOption(ChannelOption.AUTO_READ, true)
                                                                   .childOption(ChannelOption.SO_KEEPALIVE, true)
                                                                   .childHandler(this);

            CloudNet.getLogger().debug("Using " + (Epoll.isAvailable() ? "Epoll native transport" : "NIO transport"));
            CloudNet.getLogger().debug("Try to bind to " + connectableAddress.getHostName() + ':' + connectableAddress.getPort() + "...");

            ChannelFuture channelFuture = serverBootstrap.bind(connectableAddress.getHostName(), connectableAddress.getPort()).addListener(
                new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            System.out.println("CloudNet is listening @" + connectableAddress.getHostName() + ':' + connectableAddress.getPort());
                            CloudNet.getInstance().getCloudServers().add(CloudNetServer.this);

                        } else {
                            System.out.println("Failed to bind @" + connectableAddress.getHostName() + ':' + connectableAddress.getPort());
                        }
                    }
                }).addListener(ChannelFutureListener.CLOSE_ON_FAILURE).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);

            channelFuture.sync().channel().closeFuture();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public SslContext getSslContext() {
        return sslContext;
    }

    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    @Override
    public void close() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    @Override
    protected void initChannel(Channel channel) {
        System.out.println("Channel [" + channel.remoteAddress().toString() + "] connecting...");

        ChannelConnectEvent channelConnectEvent = new ChannelConnectEvent(false, channel);
        CloudNet.getInstance().getEventManager().callEvent(channelConnectEvent);
        if (channelConnectEvent.isCancelled()) {
            channel.close().syncUninterruptibly();
            return;
        }

        String[] address = channel.remoteAddress().toString().split(":");
        String host = address[0].replaceFirst(NetworkUtils.SLASH_STRING, NetworkUtils.EMPTY_STRING);
        for (Wrapper cn : CloudNet.getInstance().getWrappers().values()) {
            if (cn.getChannel() == null && cn.getNetworkInfo().getHostName().equalsIgnoreCase(host)) {
                if (sslContext != null) {
                    channel.pipeline().addLast(sslContext.newHandler(channel.alloc()));
                }

                NetworkUtils.initChannel(channel);
                channel.pipeline().addLast("client", new CloudNetClientAuth(channel, this));
                return;
            }

            if (cn.getNetworkInfo().getHostName().equals(host)) {
                if (sslContext != null) {
                    channel.pipeline().addLast(sslContext.newHandler(channel.alloc()));
                }

                NetworkUtils.initChannel(channel);
                CloudNetClientAuth cloudNetProxyClientAuth = new CloudNetClientAuth(channel, this);
                channel.pipeline().addLast("client", cloudNetProxyClientAuth);
                return;
            }
        }

        channel.close().addListener(ChannelFutureListener.CLOSE_ON_FAILURE).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

    }
}
