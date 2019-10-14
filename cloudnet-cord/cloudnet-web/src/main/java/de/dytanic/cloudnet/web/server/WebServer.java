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
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
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
    protected EventLoopGroup acceptorGroup = NetworkUtils.eventLoopGroup();

    /**
     * The child event loop group to the acceptor group, handling channels.
     */
    protected EventLoopGroup workerGroup = NetworkUtils.eventLoopGroup();

    /**
     * The SSL context with certificate and private key, when SSL is enabled.
     *
     * @see #ssl
     */
    protected SslContext sslContext;

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
     * @param ssl  whether to use SSL with a self-signed certificate
     * @param host the host this web server is bound to.
     * @param port the port this web server is bound to.
     *
     * @throws CertificateException thrown when the certificate could not
     *                              be generated.
     * @throws SSLException         thrown when an error during the creation of the
     *                              ssl context occurred.
     */
    public WebServer(boolean ssl, String host, int port) throws CertificateException, SSLException {
        this.ssl = ssl;
        this.address = host;
        this.port = port;
        this.webServerProvider = new WebServerProvider();

        if (ssl) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslContext = SslContextBuilder.forServer(ssc.key(), ssc.cert()).build();
        }

        serverBootstrap = new ServerBootstrap().group(acceptorGroup, workerGroup)
                                               .childOption(ChannelOption.IP_TOS, 24)
                                               .childOption(ChannelOption.TCP_NODELAY,
                                                            true)
                                               .childOption(ChannelOption.AUTO_READ, true)
                                               .channel(NetworkUtils.serverSocketChannel())
                                               .childHandler(new ChannelInitializer<Channel>() {
                                                   @Override
                                                   protected void initChannel(Channel channel) {
                                                       if (sslContext != null) {
                                                           channel.pipeline().addLast(sslContext.newHandler(channel.alloc()));
                                                       }

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

    public SslContext getSslContext() {
        return sslContext;
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
     * @throws InterruptedException thrown when the synchronous call is interrupted.
     */
    public void bind() throws InterruptedException {
        System.out.println("Bind WebServer at [" + address + ':' + port + ']');
        serverBootstrap.bind(address, port).sync().channel().closeFuture();
    }
}
