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
import eu.cloudnetservice.cloudnet.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;

public final class CommandList extends Command {

    public CommandList() {
        super("list", "cloudnet.command.list");

        description = "Lists some information of the network";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        sender.sendMessage("CloudNet: ");
        int memory = 0, maxMemory = 0;
        for (Wrapper wrapper : CloudNet.getInstance().getWrappers().values()) {
            memory += wrapper.getUsedMemory();
            maxMemory += wrapper.getMaxMemory();

            sender.sendMessage("Wrapper: [" + wrapper.getServerId() + "] @" + wrapper.getNetworkInfo().getHostName());
            sender.sendMessage("Info: CPU Usage: " + NetworkUtils.DECIMAL_FORMAT.format(wrapper.getCpuUsage()) + "/100% | Memory: " + wrapper
                .getUsedMemory() + NetworkUtils.SLASH_STRING + wrapper.getMaxMemory() + "MB");
            if (wrapper.getWrapperInfo() != null) {
                sender.sendMessage("CloudNet-Wrapper Version: " + wrapper.getWrapperInfo().getVersion());
            }

            sender.sendMessage(" ", "Proxys:");
            for (ProxyServer proxyServer : wrapper.getProxies().values()) {
                sender.sendMessage("Proxy [" + proxyServer.getServerId() + "] @" + proxyServer.getNetworkInfo()
                                                                                              .getHostName() + " | " + proxyServer.getProxyInfo()
                                                                                                                                  .getOnlineCount() + NetworkUtils.SLASH_STRING + CloudNet
                    .getInstance()
                    .getProxyGroups()
                    .get(proxyServer.getProxyInfo().getServiceId().getGroup())
                    .getProxyConfig()
                    .getMaxPlayers() + " | State: " + (proxyServer.getChannel() != null ? "connected" : "not connected"));
            }
            sender.sendMessage(" ", "Servers:");
            for (MinecraftServer proxyServer : wrapper.getServers().values()) {
                sender.sendMessage("Server [" + proxyServer.getServerId() + "] @" + proxyServer.getServerInfo()
                                                                                               .getHost() + " | " + proxyServer.getServerInfo()
                                                                                                                               .getOnlineCount() + NetworkUtils.SLASH_STRING + proxyServer
                    .getServerInfo()
                    .getMaxPlayers() + " | State: " + (proxyServer.getChannel() != null ? "connected" : "not connected"));
            }
            sender.sendMessage(" ");
        }

        for (CloudPlayer cloudPlayer : CloudNet.getInstance().getNetworkManager().getOnlinePlayers().values()) {
            sender.sendMessage("* " + cloudPlayer.getUniqueId() + '#' + cloudPlayer.getName() + " - " + cloudPlayer.getProxy() + ':' + cloudPlayer
                .getServer());
        }

        sender.sendMessage(" ");

        sender.sendMessage("CloudNet uses " + memory + NetworkUtils.SLASH_STRING + maxMemory + "MB ");

    }
}
