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

import java.util.UUID;

public class CommandList extends Command {

    public CommandList()
    {
        super("list", "cloudnet.command.list");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        sender.sendMessage("CloudNet: ");
        int memory = 0, maxMemory = 0;
        for(Wrapper wrapper : CloudNet.getInstance().getWrappers().values())
        {
            memory += wrapper.getUsedMemory();
            maxMemory += wrapper.getMaxMemory();

            sender.sendMessage("Wrapper: [" + wrapper.getServerId() + "] @" + wrapper.getNetworkInfo().getHostName());
            sender.sendMessage("Info: CPU Usage: " + NetworkUtils.DECIMAL_FORMAT.format(wrapper.getCpuUsage()) + "/100% | Memory: " + wrapper.getUsedMemory() + "/" + wrapper.getMaxMemory() + "MB");

            sender.sendMessage(" ", "Proxys:");
            for(ProxyServer proxyServer : wrapper.getProxys().values())
            {
                sender.sendMessage("Proxy [" + proxyServer.getServerId() + "] @" + proxyServer.getNetworkInfo().getHostName() + " | " + proxyServer.getProxyInfo().getOnlineCount() + "/" + CloudNet.getInstance().getProxyGroups().get(proxyServer.getProxyInfo().getServiceId().getGroup()).getProxyConfig().getMaxPlayers() + " | State: " + (proxyServer.getChannel() != null ? "connected" : "not connected"));
            }
            sender.sendMessage(" ", "Servers:");
            for(MinecraftServer proxyServer : wrapper.getServers().values())
            {
                sender.sendMessage("Server [" + proxyServer.getServerId() + "] @" + proxyServer.getServerInfo().getHost() + " | " + proxyServer.getServerInfo().getOnlineCount() + "/" + proxyServer.getServerInfo().getMaxPlayers() + " | State: " + (proxyServer.getChannel() != null ? "connected" : "not connected"));
            }
            sender.sendMessage(" ");
        }

        for(CloudPlayer cloudPlayer : CloudNet.getInstance().getNetworkManager().getOnlinePlayers().values())
        {
            sender.sendMessage("* " + cloudPlayer.getUniqueId() + "#" + cloudPlayer.getName() + " - " + cloudPlayer.getProxy() + ":" + cloudPlayer.getServer());
        }

        sender.sendMessage(" ");

        sender.sendMessage("CloudNet uses " + memory + "/" + maxMemory + "MB ");

    }
}