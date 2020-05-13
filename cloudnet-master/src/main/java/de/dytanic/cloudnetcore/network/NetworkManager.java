package de.dytanic.cloudnetcore.network;

import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.MultiValue;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.WrapperInfo;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.PlayerCommandExecution;
import de.dytanic.cloudnet.lib.player.PlayerConnection;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnet.lib.server.SimpleServerGroup;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.screen.ScreenInfo;
import de.dytanic.cloudnet.lib.service.wrapper.WrapperScreen;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.api.event.network.CustomChannelMessageEvent;
import de.dytanic.cloudnetcore.api.event.network.UpdateAllEvent;
import de.dytanic.cloudnetcore.api.event.network.WrapperLineInputEvent;
import de.dytanic.cloudnetcore.api.event.player.*;
import de.dytanic.cloudnetcore.api.event.server.*;
import de.dytanic.cloudnetcore.database.PlayerDatabase;
import de.dytanic.cloudnetcore.database.StatisticManager;
import de.dytanic.cloudnetcore.network.components.INetworkComponent;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.network.components.util.ChannelFilter;
import de.dytanic.cloudnetcore.network.packet.out.*;
import de.dytanic.cloudnetcore.util.MessageConfig;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tareko on 19.07.2017.
 */
public final class NetworkManager {

    private java.util.Map<UUID, CloudPlayer> waitingPlayers = new ConcurrentHashMap<>();
    private java.util.Map<UUID, CloudPlayer> onlinePlayers = new ConcurrentHashMap<>();
    private Document moduleProperties = new Document();

    private MessageConfig messageConfig;

    public NetworkManager() {
        messageConfig = new MessageConfig();
    }

    public Map<UUID, CloudPlayer> getWaitingPlayers() {
        return waitingPlayers;
    }

    public Map<UUID, CloudPlayer> getOnlinePlayers() {
        return onlinePlayers;
    }

    public Document getModuleProperties() {
        return moduleProperties;
    }

    public MessageConfig getMessageConfig() {
        return messageConfig;
    }

    public void reload() {
        CloudNet.getInstance().getEventManager().callEvent(new UpdateAllEvent(this, true));
    }

    public void updateAll() {
        CloudNet.getInstance().getEventManager().callEvent(new UpdateAllEvent(this, true));
        PacketOutCloudNetwork cloudNetwork = new PacketOutCloudNetwork(newCloudNetwork());
        sendAll(cloudNetwork);
    }

    public CloudNetwork newCloudNetwork() {
        CloudNetwork cloudNetwork = new CloudNetwork();
        cloudNetwork.setOnlineCount(getOnlineCount());
        cloudNetwork.setMessages(messageConfig.load());
        cloudNetwork.setModules(moduleProperties);
        //cloudNetwork.setNotifySystem(CloudNet.getInstance().getConfig().isNotifyService());
        cloudNetwork.setWebPort(CloudNet.getInstance().getConfig().getWebServerConfig().getPort());

        Collection<WrapperInfo> wrappers = new LinkedList<>();
        for (Wrapper wrapper : CloudNet.getInstance().getWrappers().values()) {
            if (wrapper.getWrapperInfo() != null) {
                wrappers.add(wrapper.getWrapperInfo());
            }
        }
        cloudNetwork.setWrappers(wrappers);

        final Map<String, SimpleServerGroup> serverGroups = new HashMap<>();
        CloudNet.getInstance().getServerGroups().forEach(
            (serverId, serverGroup) -> serverGroups.put(serverId, serverGroup.toSimple()));

        cloudNetwork.setServerGroups(serverGroups);
        //cloudNetwork.setPermissionPool(permissionPool);
        cloudNetwork.setProxyGroups(CloudNet.getInstance().getProxyGroups());
        cloudNetwork.setModules(moduleProperties);
        cloudNetwork.setRegisteredPlayerCount(CloudNet.getInstance().getDbHandlers().getPlayerDatabase().getDatabase().size());

        return cloudNetwork;
    }

    public void handlePlayerLogout(UUID uniqueId, ProxyServer proxyServer) {
        CloudNet.getInstance().getEventManager().callEvent(new LogoutEventUnique(uniqueId));
        String name = CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase().get(uniqueId);
        System.out.println("Player [" + name + NetworkUtils.SLASH_STRING + uniqueId + "/] is disconnected on " + proxyServer.getServerId());

        this.onlinePlayers.remove(uniqueId);

        this.sendAllUpdate(new PacketOutLogoutPlayer(uniqueId));
        this.sendAll(new PacketOutUpdateOnlineCount(getOnlineCount()));
    }

    public void sendAllUpdate(Packet packet) {
        sendAll(packet, networkComponent -> {
            if (networkComponent instanceof ProxyServer) {
                return true;
            }

            if (networkComponent instanceof MinecraftServer) {
                final MinecraftServer minecraftServer = (MinecraftServer) networkComponent;
                if (minecraftServer.getGroupMode().equals(ServerGroupMode.LOBBY) ||
                    minecraftServer.getGroupMode().equals(ServerGroupMode.STATIC_LOBBY)) {
                    return true;
                }

                ServerGroup serverGroup = CloudNet.getInstance().getServerGroups().get(minecraftServer.getServiceId().getGroup());
                if (serverGroup != null) {
                    if (serverGroup.getAdvancedServerConfig().isNotifyProxyUpdates() &&
                        (packet instanceof PacketOutUpdateProxyInfo ||
                            packet instanceof PacketOutProxyAdd ||
                            packet instanceof PacketOutProxyRemove)) {
                        return true;
                    }

                    if (serverGroup.getAdvancedServerConfig().isNotifyServerUpdates() &&
                        (packet instanceof PacketOutUpdateServerInfo ||
                            packet instanceof PacketOutServerAdd ||
                            packet instanceof PacketOutServerRemove)) {
                        return true;
                    }

                    return serverGroup.getAdvancedServerConfig().isNotifyPlayerUpdatesFromNoCurrentPlayer() &&
                        (packet instanceof PacketOutUpdatePlayer ||
                            packet instanceof PacketOutLoginPlayer ||
                            packet instanceof PacketOutLogoutPlayer ||
                            packet instanceof PacketOutUpdateOfflinePlayer);

                }
            }
            return false;
        });
    }

    public NetworkManager sendAll(Packet packet) {
        sendAll(packet, networkComponent -> true);
        return this;
    }

    public void handlePlayerLogout(CloudPlayer playerWhereAmI) {
        CloudNet.getInstance().getEventManager().callEvent(new LogoutEvent(playerWhereAmI));
        try {
            System.out.printf("Player [%s/%s/%s] is disconnected on %s%n",
                              playerWhereAmI.getName(),
                              playerWhereAmI.getUniqueId(),
                              playerWhereAmI.getPlayerConnection().getHost(),
                              playerWhereAmI.getProxy());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        this.onlinePlayers.remove(playerWhereAmI.getUniqueId());

        this.sendAllUpdate(new PacketOutLogoutPlayer(playerWhereAmI));
        this.sendAll(new PacketOutUpdateOnlineCount(getOnlineCount()));

        playerWhereAmI.setLastLogin(System.currentTimeMillis());
        playerWhereAmI.setLastPlayerConnection(playerWhereAmI.getPlayerConnection());
        CloudNet.getInstance().getDbHandlers().getPlayerDatabase().updatePlayer(CloudPlayer.newOfflinePlayer(playerWhereAmI));

    }

    public int getOnlineCount() {
        return CloudNet.getInstance().getProxys().values()
                       .stream()
                       .mapToInt(proxy -> proxy.getProxyInfo().getOnlineCount())
                       .sum();
    }

    public void updateAll0() {
        CloudNet.getInstance().getEventManager().callEvent(new UpdateAllEvent(this, false));
        PacketOutCloudNetwork cloudNetwork = new PacketOutCloudNetwork(newCloudNetwork());
        sendAll(cloudNetwork);
    }

    public void handlePlayerLoginRequest(ProxyServer proxyServer, PlayerConnection cloudPlayerConnection, UUID uniqueId) {
        CloudNet.getLogger().finest("player login request " + cloudPlayerConnection.getName() + '#' + uniqueId + " onlinePlayers");
        if (this.onlinePlayers.containsKey(cloudPlayerConnection.getUniqueId())) {
            proxyServer.sendPacketSynchronized(new PacketOutLoginPlayer(uniqueId, null, "Already connected in network"));
            return;
        }

        CloudNet.getLogger().finest("player login request " + cloudPlayerConnection.getName() + '#' + uniqueId + " call LoginRequestEvent");
        LoginRequestEvent loginRequestEvent = new LoginRequestEvent(proxyServer, cloudPlayerConnection);
        CloudNet.getInstance().getEventManager().callEvent(loginRequestEvent);

        PlayerDatabase playerDatabase = CloudNet.getInstance().getDbHandlers().getPlayerDatabase();
        OfflinePlayer offlinePlayer = null;

        CloudNet.getLogger().finest("player login request " + cloudPlayerConnection.getName() + '#' + uniqueId + " database contains");
        if (!playerDatabase.containsPlayer(cloudPlayerConnection.getUniqueId())) {
            CloudNet.getLogger().finest("player login request " + cloudPlayerConnection.getName() + '#' + uniqueId + " database register");
            offlinePlayer = playerDatabase.registerPlayer(cloudPlayerConnection);
        }

        if (offlinePlayer == null) {
            CloudNet.getLogger().finest("player login request " + cloudPlayerConnection.getName() + '#' + uniqueId + " database get");
            offlinePlayer = playerDatabase.getPlayer(cloudPlayerConnection.getUniqueId());
        }

        CloudNet.getLogger().finest("player login request " + cloudPlayerConnection.getName() + '#' + uniqueId + " create CloudPlayer");
        CloudPlayer cloudPlayer = new CloudPlayer(offlinePlayer, cloudPlayerConnection, proxyServer.getServerId());

        if (cloudPlayer.getFirstLogin() == null) {
            CloudNet.getLogger().finest("player login request " + cloudPlayerConnection.getName() + '#' + uniqueId + " set firstLogin");
            cloudPlayer.setFirstLogin(System.currentTimeMillis());
        }

        CloudNet.getLogger().finest("player login request " + cloudPlayerConnection.getName() + '#' + uniqueId + " call PlayerInitEvent");
        CloudNet.getInstance().getEventManager().callEvent(new PlayerInitEvent(cloudPlayer));

        CloudNet.getLogger()
                .finest("player login request " + cloudPlayerConnection.getName() + '#' + uniqueId + " NameToUUIDDatabase append");
        CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase().append(new MultiValue<>(cloudPlayerConnection.getName(),
                                                                                               cloudPlayerConnection.getUniqueId()));
        CloudNet.getLogger()
                .finest("player login request " + cloudPlayerConnection.getName() + '#' + uniqueId + " NameToUUIDDatabase replace");
        CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase().replace(new MultiValue<>(cloudPlayerConnection.getUniqueId(),
                                                                                                cloudPlayerConnection.getName()));

        CloudNet.getLogger().finest("player login request " + cloudPlayerConnection.getName() + '#' + uniqueId + " setName");
        cloudPlayer.setName(cloudPlayerConnection.getName());
        CloudNet.getLogger().finest("player login request " + cloudPlayerConnection.getName() + '#' + uniqueId + " updatePlayer");
        CloudNet.getInstance().getDbHandlers().getPlayerDatabase().updatePlayer(CloudPlayer.newOfflinePlayer(cloudPlayer));

        CloudNet.getLogger().finest("player login request " + cloudPlayerConnection.getName() + '#' + uniqueId + " PacketOutLoginPlayer");
        proxyServer.sendPacket(new PacketOutLoginPlayer(uniqueId, cloudPlayer, "successful Login"));
        CloudNet.getLogger().finest("player login request " + cloudPlayerConnection.getName() + '#' + uniqueId + " waitingPlayers");
        this.waitingPlayers.put(cloudPlayer.getUniqueId(), cloudPlayer);
        CloudNet.getLogger().finest("player login request " + cloudPlayerConnection.getName() + '#' + uniqueId + " handlePlayerLogin");
        handlePlayerLogin(cloudPlayer);
    }

    public void handlePlayerLogin(CloudPlayer loginPlayer) {

        CloudNet.getInstance().getEventManager().callEvent(new LoginEvent(loginPlayer));
        System.out.println("Player [" + loginPlayer.getName() + NetworkUtils.SLASH_STRING + loginPlayer.getUniqueId() + NetworkUtils.SLASH_STRING + loginPlayer
            .getPlayerConnection()
            .getHost() + "] is connected on " + loginPlayer.getProxy());

        this.sendAllUpdate(new PacketOutLoginPlayer(loginPlayer));
        this.sendAll(new PacketOutUpdateOnlineCount(getOnlineCount()));

        StatisticManager.getInstance().addPlayerLogin();
        StatisticManager.getInstance().highestPlayerOnlineCount(getOnlineCount());
    }

    public NetworkManager sendAll(Packet packet, ChannelFilter filter) {
        NetworkUtils.getExecutor().submit(() -> {
            for (Wrapper wrapper : CloudNet.getInstance().getWrappers().values()) {
                if (wrapper.getChannel() != null && filter.accept(wrapper)) {
                    wrapper.sendPacket(packet);
                }

                for (ProxyServer proxyServer : wrapper.getProxies().values()) {
                    if (proxyServer.getChannel() != null && filter.accept(proxyServer)) {
                        proxyServer.sendPacket(packet);
                    }
                }

                for (MinecraftServer minecraftServer : wrapper.getServers().values()) {
                    if (minecraftServer.getChannel() != null && filter.accept(minecraftServer)) {
                        minecraftServer.sendPacket(packet);
                    }
                }

            }
        });
        return this;
    }

    public void handleServerAdd(MinecraftServer minecraftServer) {
        System.out.println("Server [" + minecraftServer.getServerId() + "] is registered on CloudNet");
        CloudNet.getInstance().getEventManager().callEvent(new ServerAddEvent(minecraftServer));
        this.sendAllUpdate(new PacketOutServerAdd(minecraftServer.getServerInfo()));
        StatisticManager.getInstance().addStartedServers();
        StatisticManager.getInstance().highestServerOnlineCount(CloudNet.getInstance().getServers().size() + CloudNet.getInstance()
                                                                                                                     .getProxys()
                                                                                                                     .size());
    }

    public void handleServerInfoUpdate(MinecraftServer minecraftServer, ServerInfo incoming) {
        minecraftServer.setServerInfo(incoming);
        CloudNet.getInstance().getEventManager().callEvent(new ServerInfoUpdateEvent(minecraftServer, incoming));
        this.sendAllUpdate(new PacketOutUpdateServerInfo(incoming));

    }

    public void handleProxyInfoUpdate(ProxyServer proxyServer, ProxyInfo incoming) {
        CloudNet.getInstance().getEventManager().callEvent(new ProxyInfoUpdateEvent(proxyServer, incoming));

        Collection<UUID> players = new ArrayList<>();

        for (ProxyServer proxy : CloudNet.getInstance().getProxys().values()) {
            proxy.getProxyInfo().getPlayers().forEach((key, value) -> players.add(key));
        }

        // Remove all players that have left the cloud network but haven't been removed
        // from the NetworkManager yet.
        for (CloudPlayer cloudPlayer : this.onlinePlayers.values()) {
            if (!players.contains(cloudPlayer.getUniqueId())) {
                this.onlinePlayers.remove(cloudPlayer.getUniqueId());
            }
        }

        this.sendAllUpdate(new PacketOutUpdateProxyInfo(incoming));
        this.sendAll(new PacketOutUpdateOnlineCount(getOnlineCount()));
    }

    public void handleServerRemove(MinecraftServer minecraftServer) {
        System.out.println("Server [" + minecraftServer.getServerId() + "] is unregistered on CloudNet");
        CloudNet.getInstance().getEventManager().callEvent(new ServerRemoveEvent(minecraftServer));
        this.sendAllUpdate(new PacketOutServerRemove(minecraftServer.getServerInfo()));
    }

    public void handleProxyAdd(ProxyServer proxyServer) {
        System.out.println("Server [" + proxyServer.getServerId() + "] is registered on CloudNet");
        this.sendToLobbys(new PacketOutProxyAdd(proxyServer.getProxyInfo()));
        CloudNet.getInstance().getEventManager().callEvent(new ProxyAddEvent(proxyServer));
        StatisticManager.getInstance().addStartedProxys();
        StatisticManager.getInstance().highestServerOnlineCount(CloudNet.getInstance().getServers().size() +
                                                                    CloudNet.getInstance().getProxys().size());
    }

    public NetworkManager sendToLobbys(Packet packet) {
        sendAll(packet,
                networkComponent -> networkComponent instanceof MinecraftServer &&
                    (((MinecraftServer) networkComponent).getGroupMode().equals(ServerGroupMode.LOBBY) ||
                        ((MinecraftServer) networkComponent).getGroupMode().equals(ServerGroupMode.STATIC_LOBBY)));
        return this;
    }

    public void handleProxyRemove(ProxyServer proxyServer) {
        System.out.println("Server [" + proxyServer.getServerId() + "] is unregistered on CloudNet");
        this.sendAllUpdate(new PacketOutProxyRemove(proxyServer.getProxyInfo()));
        CloudNet.getInstance().getEventManager().callEvent(new ProxyRemoveEvent(proxyServer));

    }

    public void handleCommandExecute(PlayerCommandExecution playerCommandExecutor) {

        CloudPlayer cloudPlayer = getPlayer(playerCommandExecutor.getName());
        if (cloudPlayer != null) {
            CloudNet.getLogger().info(String.format("Player [%s] executed command [%s] on [%s/%s]",
                                                    playerCommandExecutor.getName(),
                                                    playerCommandExecutor.getCommandLine(),
                                                    cloudPlayer.getProxy(),
                                                    cloudPlayer.getServer()));
            CloudNet.getInstance().getEventManager().callEvent(new CommandExecutionEvent(playerCommandExecutor));
            StatisticManager.getInstance().playerCommandExecutions();
        }
    }

    public CloudPlayer getPlayer(String name) {
        for (final CloudPlayer player : onlinePlayers.values()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

    public void handlePlayerUpdate(CloudPlayer cloudPlayer) {
        this.onlinePlayers.put(cloudPlayer.getUniqueId(), cloudPlayer);
        CloudNet.getInstance().getEventManager().callEvent(new UpdatePlayerEvent(cloudPlayer));
        this.sendAllUpdate(new PacketOutUpdatePlayer(cloudPlayer));

        if (cloudPlayer.getServer() != null) {
            System.out.println("Player [" + cloudPlayer.getName() + NetworkUtils.SLASH_STRING + cloudPlayer.getUniqueId() + "/] update [server=" + cloudPlayer
                .getServer() + ", proxy=" + cloudPlayer.getProxy() + ", address=" + cloudPlayer.getPlayerConnection().getHost() + ']');
        }
    }

    public void handleCustomChannelMessage(String channel, String message, Document document, PacketSender packetSender) {
        CloudNet.getInstance().getEventManager().callEvent(new CustomChannelMessageEvent(packetSender, channel, message, document));
        sendAll(new PacketOutCustomChannelMessage(channel, message, document));
    }

    public void handleWrapperScreenInput(Wrapper wrapper, WrapperScreen wrapperScreen) {
        CloudNet.getInstance().getEventManager().callEvent(new WrapperLineInputEvent(wrapper, wrapperScreen));

        if (CloudNet.getInstance().getOptionSet().has("notifyWrappers")) {
            System.out.println("[WRAPPER] " + wrapper.getServerId() + ": " + wrapperScreen.getConsoleLine());
        }
    }

    public void sendProxyMessage(String channel, String message, Document document) {
        sendToProxy(new PacketOutCustomSubChannelMessage(channel, message, document));
    }

    public NetworkManager sendToProxy(Packet packet) {
        sendAll(packet, ProxyServer.class::isInstance);
        return this;
    }

    public void handleScreen(INetworkComponent iNetworkComponent, Collection<ScreenInfo> screenInfos) {
        CloudNet.getInstance().getServerLogManager().appendScreenData(screenInfos);
    }

    public NetworkManager sendWrappers(Packet packet) {
        sendAll(packet, Wrapper.class::isInstance);
        return this;
    }

    public CloudPlayer getOnlinePlayer(UUID uniqueId) {
        return onlinePlayers.get(uniqueId);
    }

    public NetworkManager handleServerUpdate(ServerInfo serverInfo) {
        sendAll(new PacketOutUpdateServerInfo(serverInfo));
        return this;
    }

}
