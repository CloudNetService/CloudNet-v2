package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
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
                                NetworkUtils.SPACE_STRING,
                                "Server: " + minecraftServer.getServiceId().getServerId(),
                                "UUID: " + minecraftServer.getServiceId().getUniqueId(),
                                "GameId: " + minecraftServer.getServiceId().getGameId(),
                                "Group: " + minecraftServer.getServiceId().getGroup(),
                                NetworkUtils.SPACE_STRING,
                                "Connection: " + (minecraftServer.getChannel() != null ? "connected" : "not connected"),
                                "State: " + (minecraftServer.getServerInfo().isOnline() ? "Online" : "Offline") + " | " + minecraftServer.getServerInfo().getServerState(),
                                "Online: " + minecraftServer.getServerInfo().getOnlineCount() + NetworkUtils.SLASH_STRING + minecraftServer.getServerInfo().getMaxPlayers(),
                                "Motd: " + minecraftServer.getServerInfo().getMotd(),
                                NetworkUtils.SPACE_STRING,
                                "Memory: " + minecraftServer.getServerInfo().getMemory(),
                                "Address: " + minecraftServer.getServerInfo().getHost(),
                                "Port: " + minecraftServer.getServerInfo().getPort(),
                                NetworkUtils.SPACE_STRING
                        );
                    }
                        break;
                    case "proxy":
                    {
                        ProxyServer proxyServer = CloudNet.getInstance().getProxy(args[1]);
                        if(proxyServer == null) return;

                        sender.sendMessage(
                                NetworkUtils.SPACE_STRING,
                                "Proxy: " + proxyServer.getServiceId().getServerId(),
                                "UUID: " + proxyServer.getServiceId().getUniqueId(),
                                "GameId: " + proxyServer.getServiceId().getGameId(),
                                "Group: " + proxyServer.getServiceId().getGroup(),
                                NetworkUtils.SPACE_STRING,
                                "Connection: " + (proxyServer.getChannel() != null ? "connected" : "not connected"),
                                "State: " + (proxyServer.getProxyInfo().isOnline() ? "Online" : "Offline"),
                                "Online: " + proxyServer.getProxyInfo().getOnlineCount(),
                                NetworkUtils.SPACE_STRING,
                                "Memory: " + proxyServer.getProxyInfo().getMemory(),
                                "Address: " + proxyServer.getProxyInfo().getHost(),
                                "Port: " + proxyServer.getProxyInfo().getPort(),
                                NetworkUtils.SPACE_STRING
                        );
                    }
                        break;
                    case "wrapper":
                    {
                        Wrapper wrapper = CloudNet.getInstance().getWrappers().get(args[1]);
                        if(wrapper == null) return;

                        sender.sendMessage(
                                NetworkUtils.SPACE_STRING,
                                "WrapperId: " + wrapper.getServerId(),
                                NetworkUtils.SPACE_STRING,
                                "Connection: " + (wrapper.getChannel() != null ? "connected" : "not connected"),
                                "Servers started: " + wrapper.getServers().size(),
                                "Proxys started: " + wrapper.getProxys().size(),
                                NetworkUtils.SPACE_STRING,
                                "Address: " + wrapper.getNetworkInfo().getHostName(),
                                "Memory: " + wrapper.getUsedMemoryAndWaitings() + NetworkUtils.SLASH_STRING + (wrapper.getWrapperInfo() != null ? wrapper.getWrapperInfo().getMemory() + NetworkUtils.EMPTY_STRING : "0") + "MB",
                                "CPU Cores: " + (wrapper.getWrapperInfo() != null ? wrapper.getWrapperInfo().getAvailableProcessors() + NetworkUtils.EMPTY_STRING : "0"),
                                "CPU Usage: " + wrapper.getCpuUsage(),
                                NetworkUtils.SPACE_STRING
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