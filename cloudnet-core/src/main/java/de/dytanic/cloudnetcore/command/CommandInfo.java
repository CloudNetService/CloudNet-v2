package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;

/**
 * Created by Tareko on 19.01.2018.
 */
public final class CommandInfo extends Command {

    public CommandInfo()
    {
        super("info", "cloudnet.command.info", "i");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 2:
            {
                switch (args[0].toLowerCase())
                {
                    case "server":
                    {
                        MinecraftServer minecraftServer = CloudNet.getInstance().getServer(args[1]);
                        if(minecraftServer == null) return;

                        sender.sendMessage(
                                " ",
                                "Server: " + minecraftServer.getServiceId().getServerId(),
                                "UUID: " + minecraftServer.getServiceId().getUniqueId(),
                                "GameId: " + minecraftServer.getServiceId().getGameId(),
                                "Group: " + minecraftServer.getServiceId().getGroup(),
                                " ",
                                "Connection: " + (minecraftServer.getChannel() != null ? "connected" : "not connected"),
                                "State: " + (minecraftServer.getServerInfo().isOnline() ? "Online" : "Offline") + " | " + minecraftServer.getServerInfo().getServerState(),
                                "Online: " + minecraftServer.getServerInfo().getOnlineCount() + "/" + minecraftServer.getServerInfo().getMaxPlayers(),
                                "Motd: " + minecraftServer.getServerInfo().getMotd(),
                                " ",
                                "Memory: " + minecraftServer.getServerInfo().getMemory(),
                                "Address: " + minecraftServer.getServerInfo().getHost(),
                                "Port: " + minecraftServer.getServerInfo().getPort(),
                                " "
                        );
                    }
                        break;
                    case "proxy":
                    {
                        ProxyServer proxyServer = CloudNet.getInstance().getProxy(args[1]);
                        if(proxyServer == null) return;

                        sender.sendMessage(
                                " ",
                                "Proxy: " + proxyServer.getServiceId().getServerId(),
                                "UUID: " + proxyServer.getServiceId().getUniqueId(),
                                "GameId: " + proxyServer.getServiceId().getGameId(),
                                "Group: " + proxyServer.getServiceId().getGroup(),
                                " ",
                                "Connection: " + (proxyServer.getChannel() != null ? "connected" : "not connected"),
                                "State: " + (proxyServer.getProxyInfo().isOnline() ? "Online" : "Offline"),
                                "Online: " + proxyServer.getProxyInfo().getOnlineCount(),
                                " ",
                                "Memory: " + proxyServer.getProxyInfo().getMemory(),
                                "Address: " + proxyServer.getProxyInfo().getHost(),
                                "Port: " + proxyServer.getProxyInfo().getPort(),
                                " "
                        );
                    }
                        break;
                    case "wrapper":
                    {
                        Wrapper wrapper = CloudNet.getInstance().getWrappers().get(args[1]);
                        if(wrapper == null) return;

                        sender.sendMessage(
                                " ",
                                "WrapperId: " + wrapper.getServerId(),
                                " ",
                                "Connection: " + (wrapper.getChannel() != null ? "connected" : "not connected"),
                                "Servers started: " + wrapper.getServers().size(),
                                "Proxys started: " + wrapper.getProxys().size(),
                                " ",
                                "Address: " + wrapper.getNetworkInfo().getHostName(),
                                "Memory: " + wrapper.getUsedMemoryAndWaitings() + "/" + (wrapper.getWrapperInfo() != null ? wrapper.getWrapperInfo().getMemory() + "" : "0") + "MB",
                                "CPU Cores: " + (wrapper.getWrapperInfo() != null ? wrapper.getWrapperInfo().getAvailableProcessors() + "" : "0"),
                                "CPU Usage: " + wrapper.getCpuUsage(),
                                " "
                        );

                    }
                        break;
                }
            }
                break;
            default:
                sender.sendMessage(
                        "info SERVER <server> | monitor all informations about a game server",
                        "info PROXY <proxy> | monitor all informations about a proxy server",
                        "info WRAPPER <wrapper-id> | monitor all informations about a wrapper"
                );
                break;
        }
    }
}