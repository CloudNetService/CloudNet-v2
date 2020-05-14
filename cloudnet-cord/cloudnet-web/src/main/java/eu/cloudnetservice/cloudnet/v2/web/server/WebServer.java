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
     * @throws InterruptedException thrown when the synchronous call is interrupted.
     */
    public void bind() throws InterruptedException {
        System.out.println("Bind WebServer at [" + address + ':' + port + ']');
        serverBootstrap.bind(address, port).sync().channel().closeFuture();
    }
}
