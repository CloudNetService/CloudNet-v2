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

package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class CommandInfo extends Command {

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public CommandInfo() {
        super("info", "cloudnet.command.info", "i");

        description = "Shows information about a server, proxy, wrapper or server group";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "server": {
                    MinecraftServer minecraftServer = CloudNet.getInstance().getServer(args[1]);
                    if (minecraftServer == null) {
                        return;
                    }

                    sender.sendMessage(NetworkUtils.SPACE_STRING,
                                       "Server: " + minecraftServer.getServiceId().getServerId(),
                                       "UUID: " + minecraftServer.getServiceId().getUniqueId(),
                                       "Group: " + minecraftServer.getServiceId().getGroup(),
                                       NetworkUtils.SPACE_STRING,
                                       "Connection: " + (minecraftServer.getChannel() != null ? "connected" : "not connected"),
                                       "State: " + (minecraftServer.getServerInfo()
                                                                   .isOnline() ? "Online" : "Offline") + " | " + minecraftServer.getServerInfo()
                                                                                                                                .getServerState(),
                                       "Online: " + minecraftServer.getServerInfo()
                                                                   .getOnlineCount() + NetworkUtils.SLASH_STRING + minecraftServer.getServerInfo()
                                                                                                                                  .getMaxPlayers(),
                                       "Motd: " + minecraftServer.getServerInfo().getMotd(),
                                       NetworkUtils.SPACE_STRING,
                                       "Memory: " + minecraftServer.getServerInfo().getMemory(),
                                       "Address: " + minecraftServer.getServerInfo().getHost(),
                                       "Port: " + minecraftServer.getServerInfo().getPort(),
                                       NetworkUtils.SPACE_STRING);
                }
                break;
                case "proxy": {
                    ProxyServer proxyServer = CloudNet.getInstance().getProxy(args[1]);
                    if (proxyServer == null) {
                        return;
                    }

                    sender.sendMessage(NetworkUtils.SPACE_STRING,
                                       "Proxy: " + proxyServer.getServiceId().getServerId(),
                                       "UUID: " + proxyServer.getServiceId().getUniqueId(),
                                       "Group: " + proxyServer.getServiceId().getGroup(),
                                       NetworkUtils.SPACE_STRING,
                                       "Connection: " + (proxyServer.getChannel() != null ? "connected" : "not connected"),
                                       "State: " + (proxyServer.getProxyInfo().isOnline() ? "Online" : "Offline"),
                                       "Online: " + proxyServer.getProxyInfo().getOnlineCount(),
                                       NetworkUtils.SPACE_STRING,
                                       "Memory: " + proxyServer.getProxyInfo().getMemory(),
                                       "Address: " + proxyServer.getProxyInfo().getHost(),
                                       "Port: " + proxyServer.getProxyInfo().getPort(),
                                       NetworkUtils.SPACE_STRING);
                }
                break;
                case "wrapper": {
                    Wrapper wrapper = CloudNet.getInstance().getWrappers().get(args[1]);
                    if (wrapper == null) {
                        return;
                    }

                    sender.sendMessage(NetworkUtils.SPACE_STRING,
                                       "WrapperId: " + wrapper.getServerId(),
                                       NetworkUtils.SPACE_STRING,
                                       "Connection: " + (wrapper.getChannel() != null ? "connected" : "not connected"),
                                       "Servers started: " + wrapper.getServers().size(),
                                       "Proxys started: " + wrapper.getProxies().size(),
                                       NetworkUtils.SPACE_STRING,
                                       "Address: " + wrapper.getNetworkInfo().getHostName(),
                                       "Memory: " + wrapper.getUsedMemoryAndWaitings() + NetworkUtils.SLASH_STRING + (wrapper.getWrapperInfo() != null ? wrapper
                                           .getWrapperInfo()
                                           .getMemory() + NetworkUtils.EMPTY_STRING : "0") + "MB",
                                       "CPU Cores: " + (wrapper.getWrapperInfo() != null ? wrapper.getWrapperInfo()
                                                                                                  .getAvailableProcessors() + NetworkUtils.EMPTY_STRING : "0"),
                                       "CPU Usage: " + wrapper.getCpuUsage(),
                                       NetworkUtils.SPACE_STRING);

                }
                break;
                case "sg": {
                    ServerGroup group = CloudNet.getInstance().getServerGroup(args[1]);
                    if (group == null) {
                        return;
                    }

                    sender.sendMessage(NetworkUtils.SPACE_STRING,
                                       "Group: " + group.getName(),
                                       "GroupMode:" + group.getGroupMode().name(),
                                       "ServerType: " + group.getServerType().name(),
                                       "JoinPower: " + group.getJoinPower(),
                                       "MaxHeapSize: " + group.getMemory() + "MB",
                                       "MinOnlineServers: " + group.getMinOnlineServers(),
                                       "MaxOnlineServers: " + group.getMaxOnlineServers(),
                                       "Wrappers: " + Arrays.toString(group.getWrapper().toArray(EMPTY_STRING_ARRAY)),
                                       group.getTemplates().stream()
                                            .map(template -> template.getName() + ':' + template.getBackend().name())
                                            .collect(Collectors.joining(", ", "Templates: ", "")),
                                       NetworkUtils.SPACE_STRING);
                }
                break;
            }
        } else {
            sender.sendMessage("info SERVER <server> | show all server informations about one Minecraft server",
                               "info PROXY <proxy> | show all proxy stats from a current BungeeCord",
                               "info WRAPPER <wrapper-id> | show all wrapper properties and stats",
                               "info SG <serverGroup> | show all properties which you set in the group");
        }
    }
}
