/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;

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
            for (ProxyServer proxyServer : wrapper.getProxys().values()) {
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
