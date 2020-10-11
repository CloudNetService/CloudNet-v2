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

package eu.cloudnetservice.cloudnet.v2.web.server;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.util.concurrent.TimeUnit;

/**
 * A simple web server class
 */
public class WebServer {

    /**
     * The address this web server is bound to.
     */
    protected String address;

    /**
     * The port this web server is bound to.
     */
    protected int port;

    /**
     * Whether SSL should be enabled.
     */
    protected boolean ssl;

    /**
     * The connection acceptor event loop group, handling server channels.
     */
    private final EventLoopGroup acceptorGroup = NetworkUtils.eventLoopGroup();

    /**
     * The child event loop group to the acceptor group, handling channels.
     */
    private final EventLoopGroup workerGroup = NetworkUtils.eventLoopGroup();

    /**
     * The web handler provider of this web server.
     */
    protected WebServerProvider webServerProvider;

    /**
     * The bootstrap of this server using Netty.
     */
    protected ServerBootstrap serverBootstrap;

    /**
     * Constructs a new web server with the given configuration.
     *
     * @param host the host this web server is bound to.
     * @param port the port this web server is bound to.
     */
    public WebServer(String host, int port) {
        this.address = host;
        this.port = port;
        this.webServerProvider = new WebServerProvider();

        serverBootstrap = new ServerBootstrap()
            .group(acceptorGroup, workerGroup)
            .childOption(ChannelOption.IP_TOS, 24)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.AUTO_READ, true)
            .channel(NetworkUtils.serverSocketChannel())
            .childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) {
                    channel.pipeline().addLast(new HttpServerCodec(),
                                               new HttpObjectAggregator(Integer.MAX_VALUE),
                                               new WebServerHandler(WebServer.this));
                }
            });
    }

    public EventLoopGroup getAcceptorGroup() {
        return acceptorGroup;
    }

    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    public int getPort() {
        return port;
    }

    public ServerBootstrap getServerBootstrap() {
        return serverBootstrap;
    }

    public String getAddress() {
        return address;
    }

    public WebServerProvider getWebServerProvider() {
        return webServerProvider;
    }

    /**
     * Shuts the event loop groups down and awaits their termination.
     */
    public void shutdown() {
        acceptorGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        try {
            acceptorGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            workerGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Actually binds the web server to the configured port.
     *
     */
    public void bind() {
        System.out.println("Bind WebServer at [" + address + ':' + port + ']');
        try {
            serverBootstrap.bind(address, port).sync().channel().closeFuture();
        } catch (InterruptedException e) {
            throw new RuntimeException("Cannot bind REST-API to the given port " + port + " and host " + address, e);
        }
    }
}
