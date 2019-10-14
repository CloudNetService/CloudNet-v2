package de.dytanic.cloudnet.lib.network;

import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.auth.Auth;
import de.dytanic.cloudnet.lib.network.auth.packetio.PacketOutAuth;
import de.dytanic.cloudnet.lib.network.protocol.IProtocol;
import de.dytanic.cloudnet.lib.network.protocol.ProtocolProvider;
import de.dytanic.cloudnet.lib.network.protocol.ProtocolRequest;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketManager;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.scheduler.TaskScheduler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.io.File;
import java.nio.file.Path;

/**
 * Created by Tareko on 22.07.2017.
 */
public final class NetworkConnection implements PacketSender {

    private final PacketManager packetManager = new PacketManager();
    private final EventLoopGroup eventLoopGroup = NetworkUtils.eventLoopGroup(4);
    private Channel channel;
    private ConnectableAddress connectableAddress;
    private long connectionTrys = 0;
    private Runnable task;
    private SslContext sslContext;

    public NetworkConnection(ConnectableAddress connectableAddress) {
        this.connectableAddress = connectableAddress;
    }

    public NetworkConnection(ConnectableAddress connectableAddress, Runnable task) {
        this.connectableAddress = connectableAddress;
        this.task = task;
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

    public SslContext getSslContext() {
        return sslContext;
    }

    public ConnectableAddress getConnectableAddress() {
        return connectableAddress;
    }

    public EventLoopGroup getEventLoopGroup() {
        return eventLoopGroup;
    }

    public long getConnectionTrys() {
        return connectionTrys;
    }

    public Runnable getTask() {
        return task;
    }

    @Override
    public String getName() {
        return "Network-Connector";
    }

    public boolean tryConnect(boolean ssl, SimpleChannelInboundHandler<Packet> default_handler, Auth auth) {
        return tryConnect(ssl, default_handler, auth, null);
    }

    public boolean tryConnect(boolean ssl, SimpleChannelInboundHandler<Packet> default_handler, Auth auth, Runnable cancelTask) {
        try {
            if (ssl) {
                sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            }

            Bootstrap bootstrap = new Bootstrap().option(ChannelOption.AUTO_READ, true)
                                                 .group(eventLoopGroup)
                                                 .handler(new ChannelInitializer<Channel>() {

                                                     @Override
                                                     protected void initChannel(Channel channel) throws Exception {

                                                         if (sslContext != null) {
                                                             channel.pipeline().addLast(sslContext.newHandler(channel.alloc(),
                                                                                                              connectableAddress.getHostName(),
                                                                                                              connectableAddress.getPort()));
                                                         }

                                                         NetworkUtils.initChannel(channel).pipeline().addLast(default_handler);

                                                     }
                                                 })
                                                 .channel(NetworkUtils.socketChannel());
            this.channel = bootstrap.connect(connectableAddress.getHostName(), connectableAddress.getPort()).sync().channel().writeAndFlush(
                new PacketOutAuth(auth)).syncUninterruptibly().channel();

            return true;
        } catch (Exception ex) {
            connectionTrys++;
            System.out.println("Failed to connect... [" + connectionTrys + ']');
            System.out.println("Error: " + ex.getMessage());

            if (this.channel != null) {
                this.channel.close();
                this.channel = null;
            }

            if (cancelTask != null) {
                cancelTask.run();
            }

            return false;
        }
    }

    public boolean tryDisconnect() {
        if (channel != null) {
            channel.close();
        }

        eventLoopGroup.shutdownGracefully();
        return false;
    }

    public void sendFile(Path path) {
        send(ProtocolProvider.getProtocol(2), path);
    }

    @Override
    public void send(Object object) {
        if (channel == null) {
            return;
        }

        if (channel.eventLoop().inEventLoop()) {
            channel.writeAndFlush(object);
        } else {
            channel.eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    channel.writeAndFlush(object);
                }
            });
        }
    }

    @Override
    public void sendSynchronized(Object object) {
        channel.writeAndFlush(object).syncUninterruptibly();
    }

    @Override
    public void sendAsynchronized(Object object) {
        TaskScheduler.runtimeScheduler().schedule(new Runnable() {
            @Override
            public void run() {
                channel.writeAndFlush(object);
            }
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
    public void sendAsynchronized(int id, Object element) {
        sendAsynchronized(new ProtocolRequest(id, element));
    }

    @Override
    public void sendAsynchronized(IProtocol iProtocol, Object element) {
        sendAsynchronized(new ProtocolRequest(iProtocol.getId(), element));
    }

    @Override
    public void sendSynchronized(int id, Object element) {
        sendSynchronized(new ProtocolRequest(id, element));
    }

    @Override
    public void sendSynchronized(IProtocol iProtocol, Object element) {
        sendSynchronized(new ProtocolRequest(iProtocol.getId(), element));
    }

    public void sendFile(File file) {
        send(ProtocolProvider.getProtocol(2), file);
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
            channel.eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    for (Packet packet : packets) {
                        channel.writeAndFlush(packet);
                    }
                }
            });
        }
    }

    @Override
    public void sendPacket(Packet packet) {
        if (channel == null) {
            return;
        }

        if (channel.eventLoop().inEventLoop()) {
            channel.writeAndFlush(packet);
        } else {
            channel.eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    channel.writeAndFlush(packet);
                }
            });
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
            channel.eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    channel.writeAndFlush(packet).syncUninterruptibly();
                }
            });
        }
    }

    public boolean isConnected() {
        return channel != null;
    }
}
