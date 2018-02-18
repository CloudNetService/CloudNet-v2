/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.web.server;

import de.dytanic.cloudnet.lib.NetworkUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * Created by Tareko on 14.09.2017.
 */
@Getter
public class WebServer {

    protected String address;
    protected int port;
    protected boolean ssl;
    protected EventLoopGroup acceptorGroup = NetworkUtils.eventLoopGroup(), workerGroup = NetworkUtils.eventLoopGroup();
    protected SslContext sslContext;

    protected WebServerProvider webServerProvider;
    protected ServerBootstrap serverBootstrap;

    public WebServer(boolean ssl, String host, int port) throws Exception
    {
        this.ssl = ssl;
        this.address = host;
        this.port = port;
        this.webServerProvider = new WebServerProvider();

        if (ssl)
        {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslContext = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
        }

        serverBootstrap = new ServerBootstrap()
                .group(acceptorGroup, workerGroup)
                .childOption(ChannelOption.IP_TOS, 24)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.AUTO_READ, true)
                .channel(NetworkUtils.serverSocketChannel())
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception
                    {
                        if (sslContext != null) channel.pipeline().addLast(sslContext.newHandler(channel.alloc()));

                        channel.pipeline().addLast(new HttpServerCodec(), new HttpObjectAggregator(Integer.MAX_VALUE), new WebServerHandler(WebServer.this));
                    }
                });
    }

    public void shutdown()
    {
        acceptorGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        try {
            acceptorGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            workerGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        }catch (InterruptedException ex) {
        }
    }

    public void bind() throws Exception
    {
        System.out.println("Bind WebServer at [" + address + ":" + port + "]");
        serverBootstrap.bind(address, port).sync().channel().closeFuture();
    }
}