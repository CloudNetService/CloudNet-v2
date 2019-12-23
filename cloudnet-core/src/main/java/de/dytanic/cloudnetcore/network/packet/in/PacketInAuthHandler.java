/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.network.auth.Auth;
import de.dytanic.cloudnet.lib.network.auth.AuthLoginResult;
import de.dytanic.cloudnet.lib.network.auth.AuthType;
import de.dytanic.cloudnet.lib.network.auth.packetio.PacketOutAuthResult;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.CloudNetClient;
import de.dytanic.cloudnetcore.network.CloudNetClientAuth;
import de.dytanic.cloudnetcore.network.components.CloudServer;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import io.netty.channel.Channel;

/**
 * Created by Tareko on 25.07.2017.
 */
public final class PacketInAuthHandler implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        Auth auth = packet.getData().getObject("auth", Auth.TYPE);
        handleAuth(auth.getType(), auth.getAuthData(), packetSender);
    }

    public static void handleAuth(AuthType authType, Document authData, PacketSender packetSender) {
        if (!(packetSender instanceof CloudNetClientAuth)) {
            return;
        }
        CloudNetClientAuth client = (CloudNetClientAuth) packetSender;
        switch (authType) {
            case CLOUD_NET: {
                String key = authData.getString("key");
                String id = authData.getString("id");

                if (CloudNet.getInstance().getWrappers().containsKey(id)) {
                    Wrapper wrapper = CloudNet.getInstance().getWrappers().get(id);
                    String wrapperKey = CloudNet.getInstance().getConfig().getWrapperKey();
                    if (wrapperKey != null && wrapper.getChannel() == null && wrapperKey.equals(key)) {
                        Channel channel = client.getChannel();
                        channel.pipeline().remove("client");
                        client.getChannel().writeAndFlush(new PacketOutAuthResult(new AuthLoginResult(true))).syncUninterruptibly();
                        channel.pipeline().addLast(new CloudNetClient(wrapper, channel));
                        return;
                    } else {
                        client.getChannel().writeAndFlush(new PacketOutAuthResult(new AuthLoginResult(false))).syncUninterruptibly();
                        if (wrapperKey != null) {
                            CloudNet.getLogger().info(
                                "Authentication failed [Invalid WrapperKey or Wrapper is already connected!]");
                        } else {
                            CloudNet.getLogger().info(
                                "Authentication failed [WrapperKey not found, please copy a wrapper key to this instance]");
                        }
                    }
                } else {
                    client.getChannel().writeAndFlush(new PacketOutAuthResult(new AuthLoginResult(false))).syncUninterruptibly();
                    client.getChannel().close().syncUninterruptibly();
                }
            }
            return;
            case GAMESERVER_OR_BUNGEE: {
                ServiceId serviceId = authData.getObject("serviceId", ServiceId.class);
                if (CloudNet.getInstance().getWrappers().containsKey(serviceId.getWrapperId())) {
                    Wrapper wrapper = CloudNet.getInstance().getWrappers().get(serviceId.getWrapperId());
                    if (wrapper.getServers().containsKey(serviceId.getServerId())) {
                        MinecraftServer minecraftServer = wrapper.getServers().get(serviceId.getServerId());
                        if (minecraftServer.getChannel() == null && minecraftServer.getServerInfo().getServiceId().getUniqueId().equals(
                            serviceId.getUniqueId())) {
                            Channel channel = client.getChannel();
                            channel.pipeline().remove("client");
                            channel.pipeline().addLast(new CloudNetClient(minecraftServer, channel));
                        }
                    } else if (wrapper.getCloudServers().containsKey(serviceId.getServerId())) {
                        CloudServer cloudServer = wrapper.getCloudServers().get(serviceId.getServerId());
                        if (cloudServer.getChannel() == null && cloudServer.getServerInfo().getServiceId().getUniqueId().equals(
                            serviceId.getUniqueId())) {
                            Channel channel = client.getChannel();
                            channel.pipeline().remove("client");
                            channel.pipeline().addLast(new CloudNetClient(cloudServer, channel));
                        }
                    } else if (wrapper.getProxys().containsKey(serviceId.getServerId())) {
                        ProxyServer proxyServer = wrapper.getProxys().get(serviceId.getServerId());
                        if (proxyServer.getChannel() == null && proxyServer.getProxyInfo().getServiceId().getUniqueId().equals(
                            serviceId.getUniqueId())) {
                            Channel channel = client.getChannel();
                            channel.pipeline().remove("client");
                            channel.pipeline().addLast(new CloudNetClient(proxyServer, channel));
                        }
                    } else {
                        client.getChannel().close().syncUninterruptibly();
                    }
                } else {
                    client.getChannel().close().syncUninterruptibly();
                }
            }
        }
    }
}
