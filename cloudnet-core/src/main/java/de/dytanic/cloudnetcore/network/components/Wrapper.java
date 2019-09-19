/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.components;

import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.cloudserver.CloudServerMeta;
import de.dytanic.cloudnet.lib.network.WrapperExternal;
import de.dytanic.cloudnet.lib.network.WrapperInfo;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.ServerProcessMeta;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.user.SimpledUser;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.Quad;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.packet.out.*;
import io.netty.channel.Channel;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Tareko on 26.05.2017.
 */
public final class Wrapper implements INetworkComponent {

    private final java.util.Map<String, ProxyServer> proxys = NetworkUtils.newConcurrentHashMap();
    private final java.util.Map<String, MinecraftServer> servers = NetworkUtils.newConcurrentHashMap();
    private final java.util.Map<String, CloudServer> cloudServers = NetworkUtils.newConcurrentHashMap();
    // Group, ServiceId
    private final java.util.Map<String, Quad<Integer, Integer, ServiceId, Template>> waitingServices = NetworkUtils.newConcurrentHashMap();
    private Channel channel;
    private WrapperInfo wrapperInfo;
    private WrapperMeta networkInfo;
    private boolean ready;
    private double cpuUsage = -1;
    private int maxMemory = 0;

    private String serverId;

    public Wrapper(WrapperMeta networkInfo) {
        this.serverId = networkInfo.getId();
        this.networkInfo = networkInfo;
    }

    public int getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(int maxMemory) {
        this.maxMemory = maxMemory;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Map<String, CloudServer> getCloudServers() {
        return cloudServers;
    }

    public Map<String, MinecraftServer> getServers() {
        return servers;
    }

    public Map<String, ProxyServer> getProxys() {
        return proxys;
    }

    public Map<String, Quad<Integer, Integer, ServiceId, Template>> getWaitingServices() {
        return waitingServices;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public Wrapper getWrapper() {
        return this;
    }

    @Override
    public String getServerId() {
        return serverId;
    }

    public WrapperInfo getWrapperInfo() {
        return wrapperInfo;
    }

    public void setWrapperInfo(WrapperInfo wrapperInfo) {
        this.wrapperInfo = wrapperInfo;
    }

    public WrapperMeta getNetworkInfo() {
        return networkInfo;
    }

    @Override
    public String getName() {
        return serverId;
    }

    public int getUsedMemoryAndWaitings() {
        AtomicInteger integer = new AtomicInteger(getUsedMemory());

        CollectionWrapper.iterator(this.waitingServices.values(), new Runnabled<Quad<Integer, Integer, ServiceId, Template>>() {
            @Override
            public void run(Quad<Integer, Integer, ServiceId, Template> obj) {
                integer.addAndGet(obj.getSecond());
            }
        });

        return integer.get();
    }

    public int getUsedMemory() {
        int mem = 0;

        for (ProxyServer proxyServer : proxys.values()) {
            mem = mem + proxyServer.getProxyInfo().getMemory();
        }

        for (MinecraftServer proxyServer : servers.values()) {
            mem = mem + proxyServer.getProcessMeta().getMemory();
        }

        return mem;
    }

    public void sendCommand(String commandLine) {
        sendPacket(new PacketOutExecuteCommand(commandLine));
    }

    public void disconnct() {
        this.wrapperInfo = null;
        this.maxMemory = 0;
        for (MinecraftServer minecraftServer : servers.values()) {
            try {
                minecraftServer.disconnect();
            } catch (Exception ex) {

            }
        }

        for (CloudServer cloudServer : cloudServers.values()) {
            try {
                cloudServer.disconnect();
            } catch (Exception ex) {

            }
        }

        for (ProxyServer minecraftServer : proxys.values()) {
            try {
                minecraftServer.disconnect();
            } catch (Exception ex) {

            }
        }

        waitingServices.clear();
        servers.clear();
        cloudServers.clear();
        proxys.clear();
    }

    public Wrapper updateWrapper() {

        if (getChannel() == null) {
            return this;
        }

        java.util.Map<String, ServerGroup> groups = NetworkUtils.newConcurrentHashMap();
        for (ServerGroup serverGroup : CloudNet.getInstance().getServerGroups().values()) {
            if (serverGroup.getWrapper().contains(networkInfo.getId())) {
                groups.put(serverGroup.getName(), serverGroup);
                sendPacket(new PacketOutCreateTemplate(serverGroup));
            }
        }

        java.util.Map<String, ProxyGroup> proxyGroups = NetworkUtils.newConcurrentHashMap();
        for (ProxyGroup serverGroup : CloudNet.getInstance().getProxyGroups().values()) {
            if (serverGroup.getWrapper().contains(networkInfo.getId())) {
                proxyGroups.put(serverGroup.getName(), serverGroup);
                sendPacket(new PacketOutCreateTemplate(serverGroup));
            }
        }

        SimpledUser simpledUser = null;
        User user = CollectionWrapper.filter(CloudNet.getInstance().getUsers(), new Acceptable<User>() {
            @Override
            public boolean isAccepted(User value) {
                return networkInfo.getUser().equals(value.getName());
            }
        });
        if (user != null) {
            simpledUser = user.toSimple();
        }

        WrapperExternal wrapperExternal = new WrapperExternal(CloudNet.getInstance().getNetworkManager().newCloudNetwork(),
                                                              simpledUser,
                                                              groups,
                                                              proxyGroups);
        sendPacket(new PacketOutWrapperInfo(wrapperExternal));
        return this;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void writeCommand(String commandLine) {
        sendPacket(new PacketOutExecuteCommand(commandLine));
    }

    public void writeServerCommand(String commandLine, ServerInfo serverInfo) {
        sendPacket(new PacketOutExecuteServerCommand(serverInfo, commandLine));
    }

    public void writeProxyCommand(String commandLine, ProxyInfo proxyInfo) {
        sendPacket(new PacketOutExecuteServerCommand(proxyInfo, commandLine));
    }

    public Collection<Integer> getBinndedPorts() {
        Collection<Integer> ports = new ArrayList<>();

        for (Quad<Integer, Integer, ServiceId, Template> serviceIdValues : waitingServices.values()) {
            ports.add(serviceIdValues.getFirst());
        }

        for (MinecraftServer minecraftServer : servers.values()) {
            ports.add(minecraftServer.getProcessMeta().getPort());
        }

        for (ProxyServer proxyServer : proxys.values()) {
            ports.add(proxyServer.getProcessMeta().getPort());
        }

        return ports;
    }

    public void startProxy(ProxyProcessMeta proxyProcessMeta) {
        sendPacket(new PacketOutStartProxy(proxyProcessMeta));
        System.out.println("Proxy [" + proxyProcessMeta.getServiceId() + "] is now in " + serverId + " queue.");

        this.waitingServices.put(proxyProcessMeta.getServiceId().getServerId(),
                                 new Quad<>(proxyProcessMeta.getPort(),
                                            proxyProcessMeta.getMemory(),
                                            proxyProcessMeta.getServiceId(),
                                            null));
    }

    public void startProxyAsync(ProxyProcessMeta proxyProcessMeta) {
        sendPacket(new PacketOutStartProxy(proxyProcessMeta, true));
        System.out.println("Proxy [" + proxyProcessMeta.getServiceId() + "] is now in " + serverId + " queue.");

        this.waitingServices.put(proxyProcessMeta.getServiceId().getServerId(),
                                 new Quad<>(proxyProcessMeta.getPort(),
                                            proxyProcessMeta.getMemory(),
                                            proxyProcessMeta.getServiceId(),
                                            null));
    }

    public void startGameServer(ServerProcessMeta serverProcessMeta) {
        sendPacket(new PacketOutStartServer(serverProcessMeta));
        System.out.println("Server [" + serverProcessMeta.getServiceId() + "] is now in " + serverId + " queue.");

        this.waitingServices.put(serverProcessMeta.getServiceId().getServerId(),
                                 new Quad<>(serverProcessMeta.getPort(),
                                            serverProcessMeta.getMemory(),
                                            serverProcessMeta.getServiceId(),
                                            serverProcessMeta.getTemplate()));
    }

    public void startGameServerAsync(ServerProcessMeta serverProcessMeta) {
        sendPacket(new PacketOutStartServer(serverProcessMeta, true));
        System.out.println("Server [" + serverProcessMeta.getServiceId() + "] is now in " + serverId + " queue.");

        this.waitingServices.put(serverProcessMeta.getServiceId().getServerId(),
                                 new Quad<>(serverProcessMeta.getPort(),
                                            serverProcessMeta.getMemory(),
                                            serverProcessMeta.getServiceId(),
                                            serverProcessMeta.getTemplate()));
    }

    public void startCloudServer(CloudServerMeta cloudServerMeta) {
        sendPacket(new PacketOutStartCloudServer(cloudServerMeta));
        System.out.println("CloudServer [" + cloudServerMeta.getServiceId() + "] is now in " + serverId + " queue.");

        this.waitingServices.put(cloudServerMeta.getServiceId().getServerId(),
                                 new Quad<>(cloudServerMeta.getPort(),
                                            cloudServerMeta.getMemory(),
                                            cloudServerMeta.getServiceId(),
                                            cloudServerMeta.getTemplate()));
    }

    public void startCloudServerAsync(CloudServerMeta cloudServerMeta) {
        sendPacket(new PacketOutStartCloudServer(cloudServerMeta, true));
        System.out.println("CloudServer [" + cloudServerMeta.getServiceId() + "] is now in " + serverId + " queue.");

        this.waitingServices.put(cloudServerMeta.getServiceId().getServerId(),
                                 new Quad<>(cloudServerMeta.getPort(),
                                            cloudServerMeta.getMemory(),
                                            cloudServerMeta.getServiceId(),
                                            cloudServerMeta.getTemplate()));
    }

    public Wrapper stopServer(MinecraftServer minecraftServer) {
        if (this.servers.containsKey(minecraftServer.getServerId())) {
            sendPacket(new PacketOutStopServer(minecraftServer.getServerInfo()));
        }

        this.waitingServices.remove(minecraftServer.getServerId());
        return this;
    }

    public Wrapper stopServer(CloudServer cloudServer) {
        if (this.servers.containsKey(cloudServer.getServerId())) {
            sendPacket(new PacketOutStopServer(cloudServer.getServerInfo()));
        }

        this.waitingServices.remove(cloudServer.getServerId());
        return this;
    }

    public Wrapper stopProxy(ProxyServer proxyServer) {
        if (this.proxys.containsKey(proxyServer.getServerId())) {
            sendPacket(new PacketOutStopProxy(proxyServer.getProxyInfo()));
        }

        this.waitingServices.remove(proxyServer.getServerId());
        return this;
    }

    public Wrapper enableScreen(ServerInfo serverInfo) {
        sendPacket(new PacketOutScreen(serverInfo, DefaultType.BUKKIT, true));
        return this;
    }

    public Wrapper enableScreen(ProxyInfo serverInfo) {
        sendPacket(new PacketOutScreen(serverInfo, DefaultType.BUNGEE_CORD, true));
        return this;
    }

    public Wrapper disableScreen(ProxyInfo serverInfo) {
        sendPacket(new PacketOutScreen(serverInfo, DefaultType.BUNGEE_CORD, false));
        return this;
    }

    public Wrapper disableScreen(ServerInfo serverInfo) {
        sendPacket(new PacketOutScreen(serverInfo, DefaultType.BUKKIT, false));
        return this;
    }

    public Wrapper copyServer(ServerInfo serverInfo) {
        sendPacket(new PacketOutCopyServer(serverInfo));
        return this;
    }

    public Wrapper copyServer(ServerInfo serverInfo, Template template) {
        sendPacket(new PacketOutCopyServer(serverInfo, template));
        return this;
    }

    public void setConfigProperties(Configuration properties) {
        sendPacket(new PacketOutUpdateWrapperProperties(properties));
    }
}
