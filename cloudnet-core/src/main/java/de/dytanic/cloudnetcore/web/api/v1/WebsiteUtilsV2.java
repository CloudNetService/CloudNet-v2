package de.dytanic.cloudnetcore.web.api.v1;

/*
 * Created by Mc_Ruben on 13.07.2018
 *
 */


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.MalformedJsonException;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.hash.DyHash;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnet.lib.proxylayout.ProxyConfig;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.WebHandler;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.database.StatisticManager;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.player.CorePlayerExecutor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

@lombok.ToString
@lombok.Getter
public class WebsiteUtilsV2 extends WebHandler {
    public WebsiteUtilsV2() {
        super("/cloudnet/api/v2/util");
    }

    @Override
    public FullHttpResponse handleRequest(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        CloudNet.getLogger().debug("HTTP Request from " + channelHandlerContext.channel().remoteAddress());

        FullHttpResponse response = new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.OK);
        response.headers().set("Content-Type", "application/json");
        Document responseDocument = new Document().append("success", false).append("response", "");

        if (!httpRequest.headers().contains("-Xcloudnet-user")
                || (!httpRequest.headers().contains("-Xcloudnet-token") && !httpRequest.headers().contains("-Xcloudnet-password"))
                || !httpRequest.headers().contains("-Xmessage")
                || !httpRequest.headers().contains("-Xvalue")) {
            responseDocument.append("reason", "-Xcloudnet-user, -Xcloudnet-token, -Xmessage or -Xvalue not found");
            response.content().writeBytes(responseDocument.toBytesAsUTF_8());
            return response;
        }

        if (httpRequest.headers().contains("-Xcloudnet-token")) {
            if (!CloudNet.getInstance().authorization(httpRequest.headers().get("-Xcloudnet-user"), httpRequest.headers().get("-Xcloudnet-token"))) {
                responseDocument.append("reason", "-Xcloudnet-user and -Xcloudnet-token are not correct");
                response.content().writeBytes(responseDocument.toBytesAsUTF_8());
                return response;
            }
        } else if (httpRequest.headers().contains("-Xcloudnet-password")) {
            if (!CloudNet.getInstance().authorizationPassword(httpRequest.headers().get("-Xcloudnet-user"), httpRequest.headers().get("-Xcloudnet-password"))) {
                responseDocument.append("reason", "-Xcloudnet-user and -Xcloudnet-password are not correct");
                response.content().writeBytes(responseDocument.toBytesAsUTF_8());
                return response;
            }
            return response;
        } else {
            return response;
        }

        String message = httpRequest.headers().get("-Xmessage");
        String value = httpRequest.headers().get("-Xvalue");

        switch (message.toLowerCase()) {

            case "authorize":
            {
                boolean auth = false;
                String password = httpRequest.headers().get("-Xpassword");
                String hashedPassword = DyHash.hashString(password);
                for (User user : CloudNet.getInstance().getUsers()) {
                    if (user.getName().equals(value) && (user.getHashedPassword().equals(hashedPassword) || user.getApiToken().equals(password))) {
                        auth = true;
                        break;
                    }
                }
                responseDocument.append("success", true).append("response", auth);
            }
            break;

            case "startserver":
            {
                ServerGroup serverGroup = CloudNet.getInstance().getServerGroup(value);
                if (serverGroup == null) {
                    responseDocument.append("reason", "The ServerGroup \"" + value + "\" was not found");
                    break;
                }

                String amount = httpRequest.headers().get("-Xamount");

                if (amount != null && NetworkUtils.checkIsNumber(amount))
                    for (int i = 0; i < Integer.parseInt(amount); i++)
                        CloudNet.getInstance().startGameServer(serverGroup);
                else
                    CloudNet.getInstance().startGameServer(serverGroup);

                responseDocument.append("success", true);
            }
            break;

            case "stopserver":
            {
                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(value);
                if (minecraftServer == null) {
                    responseDocument.append("reason", "The Server \"" + value + "\" was not found");
                    break;
                }

                CloudNet.getInstance().stopServer(minecraftServer);

                responseDocument.append("success", true);
            }
            break;

            case "startproxy":
            {
                ProxyGroup proxyGroup = CloudNet.getInstance().getProxyGroup(value);
                if (proxyGroup == null) {
                    responseDocument.append("reason", "The ProxyGroup \"" + value + "\" was not found");
                    break;
                }

                String amount = httpRequest.headers().get("-Xamount");

                if (amount != null && NetworkUtils.checkIsNumber(amount))
                    for (int i = 0; i < Integer.parseInt(amount); i++)
                        CloudNet.getInstance().startProxy(proxyGroup);
                else
                    CloudNet.getInstance().startProxy(proxyGroup);

                responseDocument.append("success", true);
            }
            break;

            case "stopproxy":
            {
                ProxyServer proxyServer = CloudNet.getInstance().getProxy(value);
                if (proxyServer == null) {
                    responseDocument.append("reason", "The Proxy \"" + value + "\" was not found");
                    break;
                }

                CloudNet.getInstance().stopProxy(proxyServer);

                responseDocument.append("success", true);
            }
            break;

            case "onlinecount":
            {
                responseDocument.append("success", true).append("onlineCount", CloudNet.getInstance().getNetworkManager().getOnlineCount());
            }
            break;

            case "dispatchcloudcommand":
            {
                CloudNet.getInstance().getCommandManager().dispatchCommand(value);
                responseDocument.append("success", true);
            }
            break;

            case "dispatchservercommand":
            {
                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(value);
                if (minecraftServer == null) {
                    responseDocument.append("reason", "The Server \"" + value + "\" was not found");
                    break;
                }

                minecraftServer.getWrapper().writeServerCommand(httpRequest.headers().get("-Xcommand"), minecraftServer.getServerInfo());
                responseDocument.append("success", true);
            }
            break;

            case "dispatchproxycommand":
            {
                ProxyServer proxyServer = CloudNet.getInstance().getProxy(value);
                if (proxyServer == null) {
                    responseDocument.append("reason", "The Proxy \"" + value + "\" was not found");
                    break;
                }

                proxyServer.getWrapper().writeProxyCommand(httpRequest.headers().get("-Xcommand"), proxyServer.getProxyInfo());
                responseDocument.append("success", true);
            }
            break;

            case "servergroup":
            {
                responseDocument.append("success", true).append("response", CloudNet.getInstance().getServerGroup(value));
            }
            break;

            case "servergroups":
            {
                responseDocument.append("success", true).append("response", CloudNet.getInstance().getServerGroups().values());
            }
            break;

            case "proxygroup":
            {
                responseDocument.append("success", true).append("response", CloudNet.getInstance().getProxyGroup(value));
            }
            break;

            case "proxygroups":
            {
                responseDocument.append("success", true).append("response", CloudNet.getInstance().getProxyGroups().values());
            }
            break;

            case "proxynames":
            {
                if (value.isEmpty()) {
                    responseDocument.append("success", true).append("response", CloudNet.getInstance().getProxysByName());
                } else {
                    Collection<String> servers = new LinkedList<>();
                    for (ProxyServer minecraftServer : CloudNet.getInstance().getProxys().values())
                        if (minecraftServer.getServiceId().getGroup().equalsIgnoreCase(value))
                            servers.add(minecraftServer.getServiceId().getServerId());
                    responseDocument.append("success", true).append("response", servers);
                }
            }
            break;

            case "servernames":
            {
                if (value.isEmpty()) {
                    responseDocument.append("success", true).append("response", CloudNet.getInstance().getServersByName());
                } else {
                    Collection<String> servers = new LinkedList<>();
                    for (MinecraftServer minecraftServer : CloudNet.getInstance().getServers().values())
                        if (minecraftServer.getServiceId().getGroup().equalsIgnoreCase(value))
                            servers.add(minecraftServer.getServiceId().getServerId());
                    responseDocument.append("success", true).append("response", servers);
                }
            }
            break;

            case "proxyinfos":
            {
                if (value.isEmpty()) {
                    responseDocument.append("success", true).append("response", CollectionWrapper.transform(CloudNet.getInstance().getProxys().values(), ProxyServer::getProxyInfo));
                } else {
                    Collection<ProxyInfo> servers = new LinkedList<>();
                    for (ProxyServer minecraftServer : CloudNet.getInstance().getProxys().values())
                        if (minecraftServer.getServiceId().getGroup().equalsIgnoreCase(value))
                            servers.add(minecraftServer.getProxyInfo());
                    responseDocument.append("success", true).append("response", servers);
                }
            }
            break;

            case "serverinfos":
            {
                if (value.isEmpty()) {
                    responseDocument.append("success", true).append("response", CollectionWrapper.transform(CloudNet.getInstance().getServers().values(), MinecraftServer::getServerInfo));
                } else {
                    Collection<ServerInfo> servers = new LinkedList<>();
                    for (MinecraftServer minecraftServer : CloudNet.getInstance().getServers().values())
                        if (minecraftServer.getServiceId().getGroup().equalsIgnoreCase(value))
                            servers.add(minecraftServer.getServerInfo());
                    responseDocument.append("success", true).append("response", servers);
                }
            }
            break;

            case "serverinfo":
            {
                MinecraftServer proxyServer = CloudNet.getInstance().getServer(value);
                if (proxyServer == null) {
                    responseDocument.append("reason", "The Server was not found");
                    break;
                }
                responseDocument.append("success", true).append("response", proxyServer.getServerInfo());
            }
            break;

            case "proxyinfo":
            {
                ProxyServer proxyServer = CloudNet.getInstance().getProxy(value);
                if (proxyServer == null) {
                    responseDocument.append("reason", "The Proxy was not found");
                    break;
                }
                responseDocument.append("success", true).append("response", proxyServer.getProxyInfo());
            }
            break;

            case "updateservergroup":
            {
                ServerGroup serverGroup = Document.load(value).getObject("group", ServerGroup.TYPE);
                CloudNet.getInstance().getConfig().createGroup(serverGroup);

                CloudNet.getInstance().getServerGroups().put(serverGroup.getName(), serverGroup);
                CloudNet.getInstance().getNetworkManager().reload();
                CloudNet.getInstance().getNetworkManager().updateAll0();
                CloudNet.getInstance().toWrapperInstances(serverGroup.getWrapper()).forEach(Wrapper::updateWrapper);
                responseDocument.append("success", true);
            }
            break;

            case "updateproxygroup":
            {
                ProxyGroup proxyGroup = Document.load(value).getObject("group", ProxyGroup.class);
                CloudNet.getInstance().getConfig().createGroup(proxyGroup);

                CloudNet.getInstance().getProxyGroups().put(proxyGroup.getName(), proxyGroup);
                CloudNet.getInstance().getNetworkManager().reload();
                CloudNet.getInstance().getNetworkManager().updateAll0();
                CloudNet.getInstance().toWrapperInstances(proxyGroup.getWrapper()).forEach(Wrapper::updateWrapper);
                responseDocument.append("success", true);
            }
            break;

            case "deleteservergroup":
            {
                ServerGroup serverGroup = CloudNet.getInstance().getServerGroup(value);
                if (serverGroup == null)
                    break;
                CloudNet.getInstance().getConfig().deleteGroup(serverGroup);
                CloudNet.getInstance().getServerGroups().remove(serverGroup.getName());
                responseDocument.append("success", true);
            }
            break;

            case "deleteproxygroup":
            {
                ProxyGroup serverGroup = CloudNet.getInstance().getProxyGroup(value);
                if (serverGroup == null)
                    break;
                CloudNet.getInstance().getConfig().deleteGroup(serverGroup);
                CloudNet.getInstance().getProxyGroups().remove(serverGroup.getName());
                responseDocument.append("success", true);
            }
            break;

            case "cloudnetwork":
            {
                responseDocument.append("response", CloudNet.getInstance().getNetworkManager().newCloudNetwork());
            }
            break;

            case "statistics":
            {
                responseDocument.append("response", StatisticManager.getInstance().getStatistics().obj());
            }
            break;

            case "wrapper":
            {
                responseDocument.append("response", CloudNet.getInstance().getConfig().getWrappers());
            }
            break;

            case "onlineplayers":
            {
                responseDocument.append("response", CloudNet.getInstance().getNetworkManager().getOnlinePlayers().values());
            }
            break;

            case "permissiongroups":
            {
                PermissionPool permissionPool = CloudNet.getInstance().getNetworkManager().getModuleProperties().getObject("permissionPool", PermissionPool.TYPE);
                if (permissionPool == null || !permissionPool.isAvailable()) {
                    responseDocument.append("reason", "PermissionPool is not enabled");
                    break;
                }
                responseDocument.append("success", true).append("response", permissionPool.getGroups());
            }
            break;

            case "networkmemory":
            {
                int cpuCores = 0;
                int usedMemory = 0;
                int maxMemory = 0;
                for (Wrapper wrapper : CloudNet.getInstance().getWrappers().values()) {
                    if (wrapper.getWrapperInfo() != null) {
                        cpuCores += wrapper.getWrapperInfo().getAvailableProcessors();
                        maxMemory += wrapper.getWrapperInfo().getMemory();
                        usedMemory += wrapper.getUsedMemory();
                    }
                }
                responseDocument.append("success", true).append("response", new Document().append("usedMemory", usedMemory).append("maxMemory", maxMemory).append("cpuCores", cpuCores).obj());
            }
            break;

            case "cpucores":
            {
                int cpuCores = 0;
                for (Wrapper wrapper : CloudNet.getInstance().getWrappers().values())
                    if (wrapper.getWrapperInfo() != null)
                        cpuCores += wrapper.getWrapperInfo().getAvailableProcessors();
                responseDocument.append("success", true).append("response", cpuCores);
            }
            break;

            case "wrappers":
            {
                int connectedWrappers = 0;
                int notConnectedWrappers = 0;
                for (Wrapper wrapper : CloudNet.getInstance().getWrappers().values()) {
                    if (wrapper.getWrapperInfo() != null) {
                        connectedWrappers++;
                    } else {
                        notConnectedWrappers++;
                    }
                }

                responseDocument.append("success", true).append("response", new Document().append("connected", connectedWrappers).append("notConnected", notConnectedWrappers).obj());
            }
            break;

            case "wrapperinfos":
            {
                JsonArray jsonArray = new JsonArray();
                for (Wrapper wrapper : CloudNet.getInstance().getWrappers().values()) {
                    if (wrapper.getWrapperInfo() != null) {
                        JsonObject jsonObject = NetworkUtils.GSON.toJsonTree(wrapper.getWrapperInfo()).getAsJsonObject();
                        jsonObject.addProperty("cpuUsage", wrapper.getCpuUsage());
                        jsonObject.addProperty("usedMemory", wrapper.getUsedMemory());
                        jsonArray.add(jsonObject);
                    }
                }
                responseDocument.append("success", true).append("response", jsonArray);
            }
            break;

            case "user":
            {
                User user = CloudNet.getInstance().getUser(value);
                responseDocument.append("success", user != null).append("response", user);
            }
            break;

            case "users":
            {
                responseDocument.append("success", true).append("response", CloudNet.getInstance().getUsers());
            }
            break;

            case "updateuser":
            {
                try {
                    User user = Document.GSON.fromJson(value, User.class);
                    if (user != null) {
                        Collection<User> users = new ArrayList<>();
                        for (User x : CloudNet.getInstance().getUsers()) {
                            if (x.getName().equals(user.getName())) {
                                users.add(x);
                            }
                        }
                        if (!users.isEmpty())
                            CloudNet.getInstance().getUsers().removeAll(users);
                        CloudNet.getInstance().getUsers().add(user);
                        CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
                        responseDocument.append("success", true);
                    }
                } catch (Exception e) {
                    if (e instanceof MalformedJsonException) {
                        responseDocument.append("reason", "Malformed Json: " + e.getMessage());
                        break;
                    } else {
                        e.printStackTrace();
                        break;
                    }
                }
            }
            break;

            case "changeuserpassword":
            {
                User user = CloudNet.getInstance().getUser(value);
                if (user == null) {
                    responseDocument.append("reason", "The User was not found");
                    break;
                }

                CloudNet.getInstance().getUsers().remove(user);

                user = new User(user.getName(), user.getUniqueId(), user.getApiToken(), DyHash.hashString(httpRequest.headers().get("-Xpassword")), user.getPermissions(), user.getMetaData());

                CloudNet.getInstance().getUsers().add(user);

                responseDocument.append("success", true);
            }
            break;

            case "deleteuser":
            {
                User user = CloudNet.getInstance().getUser(value);
                boolean success = false;
                if (user != null) {
                    if (CloudNet.getInstance().getUsers().remove(user)) {
                        CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
                        success = true;
                    }
                }
                responseDocument.append("success", success);
                if (!success)
                    responseDocument.append("reason", "The User was not found");
            }
            break;

            case "playerinfo":
            {
                int onlineCount = CloudNet.getInstance().getNetworkManager().getOnlineCount();
                int maxPlayers = 0;
                for (ProxyGroup proxyGroup : CloudNet.getInstance().getProxyGroups().values()) {
                    ProxyConfig proxyConfig = proxyGroup.getProxyConfig();
                    maxPlayers += (proxyConfig.getAutoSlot().isEnabled() ? onlineCount + proxyConfig.getAutoSlot().getDynamicSlotSize() : proxyConfig.getMaxPlayers());
                }
                responseDocument.append("success", true).append("response", new Document().append("onlineCount", onlineCount).append("maxPlayers", maxPlayers).obj());
            }
            break;

            case "debugging":
            {
                responseDocument.append("success", true).append("response", CloudNet.getLogger().isDebugging());
            }
            break;

            case "permission":
            {
                if (!httpRequest.headers().contains("-Xpermission")) {
                    responseDocument.append("reason", "Missing \"-Xpermission\" header");
                    break;
                }

                User user = CloudNet.getInstance().getUser(value);
                if (user == null) {
                    responseDocument.append("reason", "The User was not found");
                    break;
                }

                responseDocument.append("success", true).append("response", user.hasPermission(httpRequest.headers().get("-Xpermission")));
            }
            break;

            case "playerbyname":
            {
                CloudPlayer cloudPlayer = CloudNet.getInstance().getNetworkManager().getPlayer(value);
                if (cloudPlayer != null) {
                    responseDocument.append("success", true).append("response", new Document("online", true).append("player", cloudPlayer).obj());
                } else {
                    UUID uniqueId = CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase().get(value);
                    OfflinePlayer offlinePlayer = uniqueId == null ? null : CloudNet.getInstance().getDbHandlers().getPlayerDatabase().getPlayer(uniqueId);
                    if (offlinePlayer != null) {
                        responseDocument.append("success", true).append("response", new Document("online", false).append("player", offlinePlayer).obj());
                    } else {
                        responseDocument.append("reason", "The Player is not registered in the database");
                    }
                }
            }
            break;

            case "playerbyuniqueid":
            {
                UUID uniqueId = UUID.fromString(value);
                CloudPlayer cloudPlayer = CloudNet.getInstance().getNetworkManager().getOnlinePlayer(uniqueId);
                if (cloudPlayer != null) {
                    responseDocument.append("success", true).append("response", new Document("online", true).append("player", cloudPlayer).obj());
                } else {
                    OfflinePlayer offlinePlayer = CloudNet.getInstance().getDbHandlers().getPlayerDatabase().getPlayer(uniqueId);
                    if (offlinePlayer != null) {
                        responseDocument.append("success", true).append("response", new Document("online", false).append("player", offlinePlayer).obj());
                    } else {
                        responseDocument.append("reason", "The Player is not registered in the database");
                    }
                }
            }
            break;

            case "sendplayer":
            {
                CloudPlayer cloudPlayer = CloudNet.getInstance().getNetworkManager().getPlayer(value);
                if (cloudPlayer == null) {
                    responseDocument.append("reason", "The player is not online");
                    break;
                }

                CorePlayerExecutor.INSTANCE.sendPlayer(cloudPlayer, httpRequest.headers().get("-Xtarget"));
                responseDocument.append("success", true);
            }
            break;

            case "shutdownwrapper":
            {
                Wrapper wrapper = CloudNet.getInstance().getWrappers().get(value);
                if (wrapper == null) {
                    responseDocument.append("reason", "The Wrapper was not found");
                    break;
                }

                wrapper.writeCommand("stop");
                responseDocument.append("success", true);
            }
            break;

            case "shutdown":
            {
                CloudNet.getInstance().shutdown();
            }
            break;

        }

        response.content().writeBytes(responseDocument.toBytesAsUTF_8());

        return response;
    }
}
